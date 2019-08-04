package core.webui.server.handlers.internals.repeatsclient;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;

public class StartRemoteClientHandler extends AbstractUIHttpHandler {

	public StartRemoteClientHandler(ObjectRenderer objectRenderer) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
		Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(request);
		if (params == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Unable to get POST paramters.");
		}
		String id = params.get("id");
		if (id.isEmpty()) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "ID must not be empty.");
		}

		backEndHolder.getPeerServiceClientManager().startClient(id);
		return renderedRepeatsRemoteClients(exchange);
	}

}
