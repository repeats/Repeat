package core.webui.server.handlers.internals.tasks;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.server.handlers.renderedobjects.RenderedRunTaskConfig;
import core.webui.webcommon.HttpServerUtilities;

public class GetRunTaskConfigHandler extends AbstractUIHttpHandler {

	public GetRunTaskConfigHandler(ObjectRenderer objectRenderer) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.GET_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
		Map<String, Object> data = new HashMap<>();
		data.put("runTaskConfig", RenderedRunTaskConfig.fromRunTaskConfig(backEndHolder.getRunActionConfig()));

		String page = objectRenderer.render("fragments/run_task_config_modal", data);
		if (page == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Failed to render page.");
		}

		return HttpServerUtilities.prepareHttpResponse(exchange, 200, page);
	}
}
