package core.webui.server.handlers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.userDefinedTask.UserDefinedAction;
import core.webcommon.HttpServerUtilities;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.server.handlers.renderedobjects.RenderedActivation;

public class TaskActivationPageHandler extends AbstractUIHttpHandler {

	public TaskActivationPageHandler(ObjectRenderer objectRenderer) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.GET_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
			throws HttpException, IOException {
		String uriString = request.getRequestLine().getUri();
		Map<String, String>  params = HttpServerUtilities.parseGetParameters(uriString);
		if (params == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Failed to parse URL " + uriString);
		}
		UserDefinedAction task = CommonTask.getTaskFromRequest(backEndHolder, params);
		if (task == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Failed to get task.");
		}

		Map<String, Object> data = new HashMap<>();
		data.put("activation", RenderedActivation.fromActivation(task.getActivation()));

		return renderedPage(exchange, "task_activation", data);
	}
}
