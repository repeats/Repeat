package cli.server.handlers;

import java.io.IOException;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpExchange;

import argo.jdom.JsonNode;
import cli.messages.TaskIdentifier;
import cli.server.CliRpcCodec;
import core.userDefinedTask.TaskGroup;
import core.userDefinedTask.UserDefinedAction;
import utilities.IoUtil;

public abstract class TaskActionHandler extends HttpHandlerWithBackend {

	private static final Logger LOGGER = Logger.getLogger(TaskActionHandler.class.getName());

	private static final String ACCEPTED_METHOD = "POST";

	@Override
	protected final void handleWithBackend(HttpExchange exchange) throws IOException {
		if (!exchange.getRequestMethod().equalsIgnoreCase(ACCEPTED_METHOD)) {
			CliRpcCodec.prepareResponse(exchange, 400, "Method must be " + ACCEPTED_METHOD);
			return;
		}

		JsonNode request = CliRpcCodec.decodeRequest(IoUtil.streamToBytes(exchange.getRequestBody()));
		if (request == null) {
			LOGGER.warning("Failed to parse request into JSON!");
			CliRpcCodec.prepareResponse(exchange, 400, "Cannot parse request!");
			return;
		}
	}

	protected abstract Void handleTaskActionWithBackend(HttpExchange exchange, JsonNode request) throws IOException;

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
