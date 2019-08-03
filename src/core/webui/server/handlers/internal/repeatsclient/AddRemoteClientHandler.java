package core.webui.server.handlers.internal.repeatsclient;

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

public class AddRemoteClientHandler extends AbstractUIHttpHandler {

	public AddRemoteClientHandler(ObjectRenderer objectRenderer) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
		Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(request);
		if (params == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Unable to get POST paramters.");
		}
		String server = params.get("server");
		int separator = server.lastIndexOf(":");
		if (separator == -1) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Server address must be in the form of host:port.");
		}
		String host = server.substring(0, separator);
		String portString = server.substring(separator + 1);
		int port = 0;
		try {
			port = Integer.parseInt(portString);
		} catch (NumberFormatException e) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Port must be integer but got " + portString + ".");
		}
		if (port <= 0 || port > 65535) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Port must be positive integer less than 65536.");
		}
		backEndHolder.getPeerServiceClientManager().addAndStartClient(host, port);
		return renderedRepeatsRemoteClients(exchange);
	}

}
