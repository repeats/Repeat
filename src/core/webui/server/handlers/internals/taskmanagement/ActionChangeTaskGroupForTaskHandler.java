package core.webui.server.handlers.internals.taskmanagement;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.CommonTask;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;

public class ActionChangeTaskGroupForTaskHandler extends AbstractUIHttpHandler {

	public ActionChangeTaskGroupForTaskHandler(ObjectRenderer objectRenderer) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
			throws HttpException, IOException {
		Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(request);
		if (params == null) {
			return HttpServerUtilities.prepareTextResponse(exchange, 400, "Failed to parse POST data.");
		}
		String groupId = CommonTask.getTaskGroupIdFromRequest(backEndHolder, params);
		if (groupId == null || groupId.isEmpty()) {
			return HttpServerUtilities.prepareTextResponse(exchange, 400, "Unable to get group ID.");
		}
		int taskIndex = CommonTask.getTaskIndexFromRequest(backEndHolder, params, backEndHolder.getCurrentTaskGroup());
		if (taskIndex == -1) {
			return HttpServerUtilities.prepareTextResponse(exchange, 400, "Unable to get task index.");
		}

		backEndHolder.changeTaskGroup(taskIndex, groupId);
		return renderedTaskForGroup(exchange);
	}
}
