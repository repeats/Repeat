package core.webui.server.handlers.internals.taskmanagement;

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

public class ActionMoveTaskUpHandler extends AbstractUIHttpHandler {

	public ActionMoveTaskUpHandler(ObjectRenderer objectRenderer) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
			throws HttpException, IOException {
		Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(request);
		if (params == null) {
			return HttpServerUtilities.prepareTextResponse(exchange, 400, "Failed to parse POST data.");
		}
		int index = CommonTask.getTaskIndexFromRequest(backEndHolder, params, backEndHolder.getCurrentTaskGroup());
		if (index == -1) {
			return HttpServerUtilities.prepareTextResponse(exchange, 400, "Cannot find task from request data.");
		}

		backEndHolder.moveTaskUp(index);
		return renderedTaskForGroup(exchange);
	}
}
