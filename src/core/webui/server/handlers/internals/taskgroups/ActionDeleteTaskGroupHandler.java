package core.webui.server.handlers.internals.taskgroups;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.webcommon.HttpServerUtilities;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.CommonTask;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;

public class ActionDeleteTaskGroupHandler extends AbstractUIHttpHandler {

	public ActionDeleteTaskGroupHandler(ObjectRenderer objectRenderer) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
			throws HttpException, IOException {
		Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(request);
		if (params == null) {
			return HttpServerUtilities.prepareTextResponse(exchange, 400, "Failed to parse POST data.");
		}
		int index = CommonTask.getTaskGroupIndexFromRequest(backEndHolder, params);
		if (index == -1) {
			return HttpServerUtilities.prepareTextResponse(exchange, 400, "Cannot find task group from request data.");
		}

		backEndHolder.removeTaskGroup(index);
		return renderedTaskGroups(exchange);
	}
}
