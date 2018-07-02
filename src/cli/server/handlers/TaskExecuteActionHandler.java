package cli.server.handlers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.nio.protocol.HttpAsyncExchange;

import argo.jdom.JsonNode;
import cli.messages.TaskExecuteMessage;
import cli.server.CliRpcCodec;
import core.controller.Core;
import core.userDefinedTask.TaskGroup;
import core.userDefinedTask.UserDefinedAction;

public class TaskExecuteActionHandler extends TaskActionHandler {

	private static final Logger LOGGER = Logger.getLogger(TaskExecuteActionHandler.class.getName());

	@Override
	protected Void handleTaskActionWithBackend(HttpAsyncExchange exchange, JsonNode request) throws IOException {
		TaskExecuteMessage message = TaskExecuteMessage.parseJSON(request);
		if (message.getTaskIdentifier() == null) {
			return CliRpcCodec.prepareResponse(exchange, 400, "Missing task identifier.");
		}

		TaskGroup group = getGroup(message.getTaskIdentifier());
		UserDefinedAction task = getTask(group, message.getTaskIdentifier());
		if (task == null) {
			return CliRpcCodec.prepareResponse(exchange, 400, "Unable to find task.");
		}

		try {
			LOGGER.info("Executing action " + task.getName());
			task.trackedAction(Core.getInstance());
		} catch (InterruptedException e) {
			LOGGER.log(Level.WARNING, "Task interrupted.", e);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Exception when executing task.", e);
			return CliRpcCodec.prepareResponse(exchange, 500, "Exception when executing task: " + e.getMessage());
		}
		return CliRpcCodec.prepareResponse(exchange, 200, "Successfully executed task.");
	}
}
