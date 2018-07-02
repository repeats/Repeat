package cli.client.handlers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import cli.CliExitCodes;
import cli.messages.TaskAddMessage;
import cli.messages.TaskExecuteMessage;
import cli.messages.TaskGroupMessage;
import cli.messages.TaskIdentifier;
import cli.messages.TaskMessage;
import cli.messages.TaskRemoveMessage;
import cli.server.CliRpcCodec;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;
import utilities.JSONUtility;
import utilities.NumberUtility;

public class TaskActionHandler extends CliActionProcessor {

	private static final Logger LOGGER = Logger.getLogger(TaskActionHandler.class.getName());

	@Override
	public void addArguments(Subparsers subparsers) {
		Subparser parser = subparsers.addParser("task").setDefault("module", "task").help("Task management.");

		parser.addArgument("-a", "--action").required(true)
        		.choices("add", "remove", "execute")
        		.help("Specify action on task.");
		parser.addArgument("-n", "--name").required(true)
				.help("Name of the task, or its index (zero based) in the group if the task exists. "
						+ "This tries to interpret this as an integer index first, then as a task name. "
						+ "For remove action, if multiple tasks share the same name, "
						+ "only the first one in the list will be removed.");
		parser.addArgument("-g", "--group").setDefault("")
				.help("Name of the group,  or its index (zero based). "
						+ "This tries to interpret this as an integer index first, then as a group name."
						+ "If not specified then the first group will be used.");
		parser.addArgument("-s", "--source_file").setDefault("")
				.help("Path to the source file. Required when adding new task.");
	}

	@Override
	public void handle(Namespace namespace) {
		String action = namespace.getString("action");
		if (action.equals("add")) {
			handleAdd(namespace);
		} else if (action.equals("remove")) {
			handleRemove(namespace);
		} else if (action.equals("execute")) {
			handleExecute(namespace);
		} else {
			LOGGER.log(Level.SEVERE, "Unknown task action " + action);
			CliExitCodes.UNKNOWN_ACTION.exit();
		}
	}

	private void handleAdd(Namespace namespace) {
		TaskGroupMessage taskGroupMessage = getGroup(namespace);
		TaskMessage taskMessage = getTask(namespace);

		String filePath = namespace.getString("source_file");
		if (!Files.isRegularFile(Paths.get(filePath))) {
			LOGGER.severe("File '" + filePath + "' does not exist.");
			CliExitCodes.INVALID_ARGUMENTS.exit();
		}

		TaskAddMessage message = TaskAddMessage.of().setFilePath(filePath)
				.setTaskIdentifier(TaskIdentifier.of().setGroup(taskGroupMessage).setTask(taskMessage));
		byte[] data = CliRpcCodec.encode(JSONUtility.jsonToString(message.jsonize()).getBytes(CliRpcCodec.ENCODING));
		sendRequest("/task/add", data);
	}

	private void handleRemove(Namespace namespace) {
		TaskGroupMessage taskGroupMessage = getGroup(namespace);
		TaskMessage taskMessage = getTask(namespace);

		TaskRemoveMessage message = TaskRemoveMessage.of()
				.setTaskIdentifier(TaskIdentifier.of().setGroup(taskGroupMessage).setTask(taskMessage));
		byte[] data = CliRpcCodec.encode(JSONUtility.jsonToString(message.jsonize()).getBytes(CliRpcCodec.ENCODING));
		sendRequest("/task/remove", data);
	}

	private void handleExecute(Namespace namespace) {
		TaskGroupMessage taskGroupMessage = getGroup(namespace);
		TaskMessage taskMessage = getTask(namespace);

		TaskExecuteMessage message = TaskExecuteMessage.of()
				.setTaskIdentifier(TaskIdentifier.of().setGroup(taskGroupMessage).setTask(taskMessage));
		byte[] data = CliRpcCodec.encode(JSONUtility.jsonToString(message.jsonize()).getBytes(CliRpcCodec.ENCODING));
		sendRequest("/task/execute", data);
	}

	private TaskGroupMessage getGroup(Namespace namespace) {
		String groupName = namespace.getString("group");
		TaskGroupMessage taskGroupMessage = TaskGroupMessage.of();
		if (NumberUtility.isNonNegativeInteger(groupName)) {
			taskGroupMessage.setIndex(Integer.parseInt(groupName));
		} else {
			taskGroupMessage.setName(groupName);
		}
		return taskGroupMessage;
	}

	private TaskMessage getTask(Namespace namespace) {
		String taskName = namespace.getString("name");
		TaskMessage taskMessage = TaskMessage.of();
		if (NumberUtility.isNonNegativeInteger(taskName)) {
			taskMessage.setIndex(Integer.parseInt(taskName));
		} else {
			taskMessage.setName(taskName);
		}
		return taskMessage;
	}

	private void sendRequest(String path, byte[] data) {
		try {
			byte[] responseData = httpClient.sendPost(path, data);
			String responseString = CliRpcCodec.decode(responseData);
			LOGGER.info(responseString);
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Encountered IOException when talking to server.", e);
			CliExitCodes.IO_EXCEPTION.exit();
		}
	}
}
