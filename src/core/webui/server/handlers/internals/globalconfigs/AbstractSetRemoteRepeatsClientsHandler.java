package core.webui.server.handlers.internals.globalconfigs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import argo.jdom.JsonNode;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;

public abstract class AbstractSetRemoteRepeatsClientsHandler extends AbstractUIHttpHandler {
	public AbstractSetRemoteRepeatsClientsHandler(ObjectRenderer objectRenderer) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected final Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
		JsonNode params = HttpServerUtilities.parsePostParameters(request);
		if (params == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Unable to get POST paramters.");
		}
		if (!params.isArrayNode("clients")) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Clients need to be an array of strings.");
		}
		List<JsonNode> clients = params.getArrayNode("clients");
		if (clients.isEmpty()) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Must use at least one client.");
		}

		List<String> clientIds = new ArrayList<>();
		try {
			clientIds = clients.stream().map(c -> c.getStringValue()).distinct().collect(Collectors.toList());
		} catch(Exception e) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Clients need to be an array of strings.");
		}
		setConfig(clientIds);
		return renderResponse(exchange);
	}

	public abstract void setConfig(List<String> clientIds);
	public abstract Void renderResponse(HttpAsyncExchange exchange) throws IOException;
}
