package cli.server.handlers;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import argo.jdom.JsonNode;
import cli.messages.TaskGroupMessage;
import cli.messages.TaskIdentifier;
import cli.server.CliRpcCodec;
import core.userDefinedTask.TaskGroup;
import core.userDefinedTask.UserDefinedAction;

public abstract class TaskActionHandler extends HttpHandlerWithBackend {

	private static final Logger LOGGER = Logger.getLogger(TaskActionHandler.class.getName());

	private static final String ACCEPTED_METHOD = "POST";

	@Override
	protected void handleWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
			throws HttpException, IOException {
		String method = request.getRequestLine().getMethod();
		if (!method.equalsIgnoreCase(ACCEPTED_METHOD)) {
			LOGGER.warning("Ignoring request with unknown method " + method);
			CliRpcCodec.prepareResponse(exchange, 400, "Method must be " + ACCEPTED_METHOD);
			return;
		}

		JsonNode requestData = CliRpcCodec.decodeRequest(getRequestBody(request));
		if (requestData == null) {
			LOGGER.warning("Failed to parse request into JSON!");
			CliRpcCodec.prepareResponse(exchange, 400, "Cannot parse request!");
			return;
		}

		handleTaskActionWithBackend(exchange, requestData);
	}

	protected abstract Void handleTaskActionWithBackend(HttpAsyncExchange exchange, JsonNode request) throws IOException;

	protected UserDefinedAction getTask(TaskGroup group, TaskIdentifier taskIdentifier) {
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

	protected TaskGroup getGroup(TaskIdentifier taskIdentifier) {
		return getGroup(taskIdentifier.getGroup());
	}

	protected TaskGroup getGroup(TaskGroupMessage taskGroup) {
		int index = taskGroup.getIndex();
		String name = taskGroup.getName();
		if (index == TaskGroupMessage.UNKNOWN_INDEX && name.isEmpty()) {
			index = 0;
		}

		TaskGroup group = null;
		if (taskGroup != null) {
			group = backEndHolder.getTaskGroup(index);
			if (group == null) {
				group = backEndHolder.getTaskGroup(name);
			}
		}
		return group;
	}
}
