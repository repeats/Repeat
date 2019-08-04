package core.webui.server.handlers.internals.globalconfigs;

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
import core.webui.server.handlers.renderedobjects.RenderedGlobalConfigs;
import core.webui.server.handlers.renderedobjects.RenderedToolsConfig;

public class GlobalConfigsPageHandler extends AbstractUIHttpHandler {

	public GlobalConfigsPageHandler(ObjectRenderer objectRenderer) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.GET_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
		Map<String, Object> data = new HashMap<>();
		RenderedToolsConfig toolsConfig = RenderedToolsConfig.of(backEndHolder.getPeerServiceClientManager(), backEndHolder.getConfig().getToolsConfig());
		data.put("globalConfigs", RenderedGlobalConfigs.of(toolsConfig));
		return renderedPage(exchange, "global_configs", data);
	}
}
