package core.cli.client.handlers;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import core.cli.CliExitCodes;
import core.cli.messages.TaskAddMessage;
import core.cli.messages.TaskExecuteMessage;
import core.cli.messages.TaskGroupMessage;
import core.cli.messages.TaskIdentifier;
import core.cli.messages.TaskListMessage;
import core.cli.messages.TaskMessage;
import core.cli.messages.TaskRemoveMessage;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;
import utilities.NumberUtility;

public class TaskCliActionHandler extends CliActionProcessor {

	private static final Logger LOGGER = Logger.getLogger(TaskCliActionHandler.class.getName());

	@Override
	public void addArguments(Subparsers subparsers) {
		Subparser parser = subparsers.addParser("task").setDefault("module", "task").help("Task management.");

		parser.addArgument("-a", "--action").required(true)
        		.choices("add", "remove", "execute", "ls", "list")
        		.help("Specify action on task.");
		parser.addArgument("-n", "--name").setDefault("")
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
		} else if (action.equals("ls") || action.equals("list")) {
			handleList(namespace);
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
		sendRequest("/task/add", message);
	}

	private void handleRemove(Namespace namespace) {
		TaskGroupMessage taskGroupMessage = getGroup(namespace);
		TaskMessage taskMessage = getTask(namespace);

		TaskRemoveMessage message = TaskRemoveMessage.of()
				.setTaskIdentifier(TaskIdentifier.of().setGroup(taskGroupMessage).setTask(taskMessage));
		sendRequest("/task/remove", message);
	}

	private void handleExecute(Namespace namespace) {
		TaskGroupMessage taskGroupMessage = getGroup(namespace);
		TaskMessage taskMessage = getTask(namespace);

		TaskExecuteMessage message = TaskExecuteMessage.of()
				.setTaskIdentifier(TaskIdentifier.of().setGroup(taskGroupMessage).setTask(taskMessage));
		sendRequest("/task/execute", message);
	}

	private void handleList(Namespace namespace) {
		TaskGroupMessage taskGroupMessage = getGroup(namespace);
		TaskListMessage message = TaskListMessage.of().setGroup(taskGroupMessage);
		sendRequest("/task/list", message);
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
		if (taskName.isEmpty()) {
			LOGGER.warning("Task must be specified.");
			CliExitCodes.INVALID_ARGUMENTS.exit();
		}

		TaskMessage taskMessage = TaskMessage.of();
		if (NumberUtility.isNonNegativeInteger(taskName)) {
			taskMessage.setIndex(Integer.parseInt(taskName));
		} else {
			taskMessage.setName(taskName);
		}
		return taskMessage;
	}
}
