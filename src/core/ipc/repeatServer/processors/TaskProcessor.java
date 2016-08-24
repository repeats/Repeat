package core.ipc.repeatServer.processors;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.ipc.repeatServer.ClientTask;
import core.ipc.repeatServer.MainMessageSender;
import core.keyChain.TaskActivation;

/**
 * This class represents the message processor for all task action.
 *
 * This module can initiate certain activities on the client:
 * 1) create task:
 * {
 * 		"task_action": "create_task",
 * 		"parameters" : [absolute path to the source file as string]
 * }
 *
 * 2) remove task:
 * {
 * 		"task_action": "remove_task",
 * 		"parameters" : [task id as integer]
 * }
 *
 * 3) run task
 * {
 * 		"task_action": "run_task",
 * 		"parameters": [task id as integer]
 * }
 *
 * All these activities will be initiated by sending a message to client, and
 * wait on the replying message with the following JSON format
 * {
 * 		"status" : status of the action on client side,
 * 		"message" : information/debug message
 * }
 *
 * @author HP Truong
 *
 */
public class TaskProcessor extends AbstractMessageProcessor {

	private static final long EXECUTION_TIMEOUT_MS = 2000;
	private final Map<Integer, ClientTask> tasks;
	private final Map<Long, Reply> locks;

	public TaskProcessor(MainMessageSender messageSender) {
		super(messageSender);
		this.tasks = new HashMap<>();
		locks = new HashMap<>();
	}

	@Override
	public boolean process(String type, long id, JsonNode content) {
		if (locks.containsKey(id)) {
			if (!verifyReplyContent(content)) {
				getLogger().warning("Invalid reply." + content + ". Drop message!");
				return false;
			}

			String status = content.getStringValue("status");
			JsonNode message = content.getNode("message");
			Reply output = locks.get(id);
			output.status = status;
			output.message = message;

			synchronized (output) {
				output.timeout = false;
				output.notify();
			}
			return true;
		}

		getLogger().warning("Unknown id " + id + ". Drop message!");
		return false;
	}

	public int createTask(File file) {
		JsonRootNode requestMessage = JsonNodeFactories.object(
				JsonNodeFactories.field("task_action", JsonNodeFactories.string("create_task")),
				JsonNodeFactories.field("parameters",
					JsonNodeFactories.array(
						JsonNodeFactories.string(file.getAbsolutePath())
					)
				)
			);

		Reply reply = fullMessage(requestMessage);
		if (reply != null && reply.status.equals(SUCCESS_STATUS)) {
			ClientTask task = ClientTask.parseJSON(reply.message);
			if (task != null) {
				this.tasks.put(task.getId(), task);
				return task.getId();
			}
		}
		return -1;
	}

	public boolean runTask(int id, TaskActivation invoker) {
		JsonRootNode requestMessage = JsonNodeFactories.object(
				JsonNodeFactories.field("task_action", JsonNodeFactories.string("run_task")),
				JsonNodeFactories.field("parameters",
					JsonNodeFactories.array(
						JsonNodeFactories.number(id),
						invoker.jsonize()
					)
				)
			);

		Reply reply = fullMessage(requestMessage);
		return reply != null && reply.status.equals(SUCCESS_STATUS);
	}

	public boolean removeTask(int id) {
		JsonRootNode requestMessage = JsonNodeFactories.object(
				JsonNodeFactories.field("task_action", JsonNodeFactories.string("remove_task")),
				JsonNodeFactories.field("parameters",
					JsonNodeFactories.array(JsonNodeFactories.number(id))
				)
			);

		Reply reply = fullMessage(requestMessage);
		if (reply.status.equals(SUCCESS_STATUS)) {
			ClientTask task = ClientTask.parseJSON(reply.message);
			if (task != null && task.getId() == id) {
				this.tasks.put(task.getId(), task);
				tasks.remove(task.getId());
				return true;
			}
		}
		return false;
	}

	private Reply fullMessage(JsonNode requestMessage) {
		if (!verifyMessageContent(requestMessage)) {
			getLogger().warning("Cannot send invalid message " + requestMessage);
			return null;
		}

		long messageId = messageSender.sendMessage(IpcMessageType.TASK.getValue(), requestMessage);
		if (messageId == -1) {
			return null;
		}

		Reply wait = new Reply();
		locks.put(messageId, wait);

		try {
			synchronized (wait) {
				wait.wait(EXECUTION_TIMEOUT_MS);
			}
			if (wait.timeout) {
				getLogger().warning("Timeout on operation with id " + messageId);
				return null;
			} else {
				return wait;
			}
		} catch (InterruptedException e) {
			getLogger().log(Level.WARNING, "Interrupted while waiting for reply", e);
			return null;
		}
	}

	@Override
	protected boolean verifyMessageContent(JsonNode content) {
		return content.isStringValue("task_action") &&
				content.isArrayNode("parameters");
	}

	private static class Reply {
		private String status;
		private JsonNode message;
		private boolean timeout;

		private Reply() {
			timeout = true;
		}
	}
}
