package core.cli.server.handlers;

import java.io.IOException;

import org.apache.http.nio.protocol.HttpAsyncExchange;

import argo.jdom.JsonNode;
import core.cli.messages.TaskExecuteMessage;
import core.cli.server.CliRpcCodec;
import core.userDefinedTask.TaskGroup;
import core.userDefinedTask.UserDefinedAction;

public class TaskRemoveActionHandler extends TaskActionHandler {

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

		backEndHolder.removeTask(task);
		return CliRpcCodec.prepareResponse(exchange, 200, "Removed task " + task.getName());
	}
}
