package core.cli.server.handlers;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import argo.jdom.JsonNode;
import core.cli.server.CliRpcCodec;
import core.webcommon.HttpHandlerWithBackend;

public abstract class SharedVariablesActionHandler extends HttpHandlerWithBackend {

	private static final Logger LOGGER = Logger.getLogger(SharedVariablesActionHandler.class.getName());

	private static final String ACCEPTED_METHOD = "POST";

	@Override
	protected final void handleWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
		String method = request.getRequestLine().getMethod();
		if (!method.equalsIgnoreCase(ACCEPTED_METHOD)) {
			LOGGER.warning("Ignoring request with unknown method " + method);
			CliRpcCodec.prepareResponse(exchange, 400, "Method must be " + ACCEPTED_METHOD);
			return;
		}

		JsonNode requestData = CliRpcCodec.decodeRequest(getRequestBody(request));
		if (requestData == null) {
			LOGGER.warning("Failed to parse request into JSON!");
			CliRpcCodec.prepareResponse(exchange, 400, "Cannot parse request!");
			return;
		}

		handleSharedVariablesActionWithBackend(exchange, requestData);
	}

	protected abstract Void handleSharedVariablesActionWithBackend(HttpAsyncExchange exchange, JsonNode requestData) throws IOException;
}
