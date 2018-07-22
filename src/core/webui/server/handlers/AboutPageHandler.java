package core.webui.server.handlers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.config.Config;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;

public class AboutPageHandler extends AbstractUIHttpHandler {

	public AboutPageHandler(ObjectRenderer objectRenderer) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.GET_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
		Map<String, Object> data = new HashMap<>();
		data.put("version", Config.RELEASE_VERSION);
		return renderedPage(exchange, "about", data);
	}
}
