package core.ipc.repeatServer.processors;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import utilities.ILoggable;
import utilities.JSONUtility;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;
import core.controller.Core;
import core.ipc.repeatServer.MainMessageSender;

/**
 * This class represents the central message processor.
 * There are four types of messages received from the client:
 * 1) action: See {@link core.ipc.repeatServer.processors.ControllerRequestProcessor}
 * 2) task: See {@link core.ipc.repeatServer.processors.TaskProcessor}
 * 3) system_host: See {@link core.ipc.repeatServer.processors.SystemRequestProcessor}
 * 4) system_client: See {@link core.ipc.repeatServer.processors.SystemRequestProcessor}
 *
 * A generic message received from client will have the following JSON format:
 * {
 * 		"type" : one of the four types above,
 * 		"id" : message id,
 * 		"content" : content to be processed by the upper layer
 * }
 *
 * Note that it is essential for a message sent with id X be replied with message of the same id from client.
 * Conversely, a message received from client with id X should also be replied with the same id to client.
 *
 * @author HP Truong
 *
 */
public class ServerMainProcessor implements ILoggable {

	public static final String TYPE_ACTION = "action";
	public static final String TYPE_TASK = "task";
	public static final String TYPE_SYSTEM_HOST = "system_host";
	public static final String TYPE_SYSTEM_CLIENT = "system_client";

	private final Map<String, AbstractMessageProcessor> messageProcesssors;
	private final ControllerRequestProcessor actionProcessor;
	private final TaskProcessor taskProcessor;
	private final SystemRequestProcessor systemProcessor;

	public ServerMainProcessor(Core core, MainMessageSender messageSender) {
		messageProcesssors = new HashMap<>();

		actionProcessor = new ControllerRequestProcessor(messageSender, core);
		taskProcessor = new TaskProcessor(messageSender);
		systemProcessor = new SystemRequestProcessor(messageSender, this);

		messageProcesssors.put(TYPE_ACTION, actionProcessor);
		messageProcesssors.put(TYPE_TASK, taskProcessor);
		messageProcesssors.put(TYPE_SYSTEM_HOST, systemProcessor);
		messageProcesssors.put(TYPE_SYSTEM_CLIENT, systemProcessor);
	}

	/**
	 * Parse a request from client.
	 * @param message request from client as JSON string
	 * @param core Core controller that will execute the action
	 * @return list of actions need to perform in order
	 */
	public boolean processRequest(String message) {
		JsonRootNode root = JSONUtility.jsonFromString(message);
		if (root == null || !verifyMessage(root)) {
			getLogger().warning("Invalid messaged received " + message);
			return false;
		}

		getLogger().fine("Receive " + message);
		String type = root.getStringValue("type");
		long id = Long.parseLong(root.getNumberValue("id"));
		JsonNode content = root.getNode("content");

		try {
			messageProcesssors.get(type).process(type, id, content);
			return true;
		} catch (InterruptedException e) {
			getLogger().log(Level.WARNING, "Interrupted while processing message", e);
			return false;
		}
	}

	private boolean verifyMessage(JsonRootNode message) {
		return message.isStringValue("type") &&
				message.isNumberValue("id") &&
				message.isObjectNode("content") &&
				messageProcesssors.containsKey(message.getStringValue("type"));
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(ServerMainProcessor.class.getName());
	}

	public TaskProcessor getTaskProcessor() {
		return taskProcessor;
	}
}
