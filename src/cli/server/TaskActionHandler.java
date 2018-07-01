package cli.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpExchange;

import argo.jdom.JsonNode;
import cli.messages.TaskAddMessage;
import cli.messages.TaskExecuteMessage;
import cli.messages.TaskIdentifier;
import core.controller.Core;
import core.userDefinedTask.TaskGroup;
import core.userDefinedTask.UserDefinedAction;
import utilities.FileUtility;

public class TaskActionHandler extends HttpHandlerWithBackend {

	private static final Logger LOGGER = Logger.getLogger(TaskActionHandler.class.getName());

	private static final String ACCEPTED_METHOD = "POST";

	@Override
	protected void handleWithBackend(HttpExchange exchange) throws IOException {
		if (!exchange.getRequestMethod().equalsIgnoreCase(ACCEPTED_METHOD)) {
			Codec.prepareResponse(exchange, 400, "Method must be " + ACCEPTED_METHOD);
			return;
		}

		String requestString = Codec.streamToString(exchange.getRequestBody());
		JsonNode request = Codec.decode(requestString);
		if (request == null) {
			LOGGER.warning("Failed to parse request into JSON!");
			Codec.prepareResponse(exchange, 400, "Cannot parse request!");
			return;
		}


		String action = "";
		try {
			action = request.getStringValue("action");
		} catch (IllegalArgumentException e) {
			LOGGER.warning("Cannot get key 'action' in request JSON.");
			Codec.prepareResponse(exchange, 400, "Cannot get key 'action' in request JSON.");
			return;
		}

		if (action.equals("add")) {
			addAction(exchange, request);
		} else if (action.equals("remove")) {
			removeAction(exchange, request);
		} else if (action.equals("execute")) {
			executeAction(exchange, request);
		} else {
			LOGGER.warning("Unknown action '" + action + "'.");
			Codec.prepareResponse(exchange, 400, "Unknown action '" + action + "'.");
			return;
		}
	}

	private Void addAction(HttpExchange exchange, JsonNode request) throws IOException {
		TaskAddMessage message = TaskAddMessage.parseJSON(request);
		if (message.getTaskIdentifier() == null) {
			return Codec.prepareResponse(exchange, 400, "Missing task identifier.");
		}

		if (message.getTaskIdentifier().getTask().getName().isEmpty()) {
			return Codec.prepareResponse(exchange, 400, "Empty task name.");
		}

		if (message.getFilePath().isEmpty()) {
			return Codec.prepareResponse(exchange, 400, "No source file.");
		}

		TaskGroup group = getGroup(message.getTaskIdentifier());
		if (group == null) {
			return Codec.prepareResponse(exchange, 400, "Unable to identify task group.");
		}


		Path path = Paths.get(message.getFilePath());
		if (!Files.isRegularFile(path)) {
			return Codec.prepareResponse(exchange, 400, "Path " + path + " is not a file.");
		}

		String source = FileUtility.readFromFile(path.toFile()).toString();
		if (source == null) {
			return Codec.prepareResponse(exchange, 500, "Unable to read source file.");
		}

		if (!backEndHolder.compileSource(source)) {
			return Codec.prepareResponse(exchange, 500, "Unable to compile source file.");
		}

		backEndHolder.addCurrentTask(group);
		return Codec.prepareResponse(exchange, 200, "Successfully added new task.");
	}

	private Void removeAction(HttpExchange exchange, JsonNode request) throws IOException {
		TaskExecuteMessage message = TaskExecuteMessage.parseJSON(request);
		if (message.getTaskIdentifier() == null) {
			return Codec.prepareResponse(exchange, 400, "Missing task identifier.");
		}

		TaskGroup group = getGroup(message.getTaskIdentifier());
		UserDefinedAction task = getTask(group, message.getTaskIdentifier());
		if (task == null) {
			return Codec.prepareResponse(exchange, 400, "Unable to find task.");
		}

		return null;
	}

	private Void executeAction(HttpExchange exchange, JsonNode request) throws IOException {
		TaskExecuteMessage message = TaskExecuteMessage.parseJSON(request);
		if (message.getTaskIdentifier() == null) {
			return Codec.prepareResponse(exchange, 400, "Missing task identifier.");
		}

		TaskGroup group = getGroup(message.getTaskIdentifier());
		UserDefinedAction task = getTask(group, message.getTaskIdentifier());
		if (task == null) {
			return Codec.prepareResponse(exchange, 400, "Unable to find task.");
		}

		try {
			task.trackedAction(Core.getInstance());
		} catch (InterruptedException e) {
			LOGGER.log(Level.WARNING, "Task interrupted.", e);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Exception when executing task.", e);
			return Codec.prepareResponse(exchange, 500, "Exception when executing task: " + e.getMessage());
		}
		return Codec.prepareResponse(exchange, 200, "Successfully executed task.");
	}

	private UserDefinedAction getTask(TaskGroup group, TaskIdentifier taskIdentifier) {
		UserDefinedAction task = null;
		if (group != null) {
			task = group.getTask(taskIdentifier.getTask().getIndex());
			if (task == null) {
				task = group.getTask(taskIdentifier.getTask().getName());
			}
			return task;
		}

		return backEndHolder.getTask(taskIdentifier.getTask().getName());
	}

	private TaskGroup getGroup(TaskIdentifier taskIdentifier) {
		TaskGroup group = null;
		if (taskIdentifier.getGroup() != null) {
			group = backEndHolder.getTaskGroup(taskIdentifier.getGroup().getIndex());
			if (group == null) {
				group = backEndHolder.getTaskGroup(taskIdentifier.getGroup().getName());
			}
		}
		return group;
	}
}
