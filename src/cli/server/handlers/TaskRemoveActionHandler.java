package cli.server.handlers;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import argo.jdom.JsonNode;
import cli.messages.TaskExecuteMessage;
import cli.server.CliRpcCodec;
import core.userDefinedTask.TaskGroup;
import core.userDefinedTask.UserDefinedAction;

public class TaskRemoveActionHandler extends TaskActionHandler {

	@Override
	protected Void handleTaskActionWithBackend(HttpExchange exchange, JsonNode request) throws IOException {
		TaskExecuteMessage message = TaskExecuteMessage.parseJSON(request);
		if (message.getTaskIdentifier() == null) {
			return CliRpcCodec.prepareResponse(exchange, 400, "Missing task identifier.");
		}

		TaskGroup group = getGroup(message.getTaskIdentifier());
		UserDefinedAction task = getTask(group, message.getTaskIdentifier());
		if (task == null) {
			return CliRpcCodec.prepareResponse(exchange, 400, "Unable to find task.");
		}

		return null;
	}

}
