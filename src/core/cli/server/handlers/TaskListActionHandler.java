package core.cli.server.handlers;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.nio.protocol.HttpAsyncExchange;

import argo.jdom.JsonNode;
import core.cli.messages.TaskGroupMessage;
import core.cli.messages.TaskListMessage;
import core.cli.server.CliRpcCodec;
import core.cli.server.utils.EnumerationUtils;
import core.userDefinedTask.TaskGroup;
import core.userDefinedTask.UserDefinedAction;

public class TaskListActionHandler extends TaskActionHandler {

	@Override
	protected Void handleTaskActionWithBackend(HttpAsyncExchange exchange, JsonNode request) throws IOException {
		TaskListMessage message = TaskListMessage.parseJSON(request);
		boolean noGroup = message.getGroup() == null ||
				(message.getGroup().getIndex() == TaskGroupMessage.UNKNOWN_INDEX &&
				message.getGroup().getName().isEmpty());

		if (noGroup) {
			List<String> names = backEndHolder.getTaskGroups().stream().map(TaskGroup::getName).collect(Collectors.toList());
			return CliRpcCodec.prepareResponse(exchange, 200, EnumerationUtils.enumerate(names));
		}
		TaskGroup group = getGroup(message.getGroup());
		if (group == null) {
			return CliRpcCodec.prepareResponse(exchange, 400, "Cannot find group with given arguments.");
		}
		List<String> taskNames = group.getTasks().stream().map(UserDefinedAction::getName).collect(Collectors.toList());
		return CliRpcCodec.prepareResponse(exchange, 200, EnumerationUtils.enumerate(taskNames));
	}
}
