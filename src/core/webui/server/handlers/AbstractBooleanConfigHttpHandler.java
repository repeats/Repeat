package core.webui.server.handlers;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.webcommon.HttpServerUtilities;

public abstract class AbstractBooleanConfigHttpHandler extends AbstractSingleMethodHttpHandler {

	public AbstractBooleanConfigHttpHandler() {
		super(AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected final Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
		Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(request);
		if (params == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Failed to parse POST parameters.");
		}
		String value = params.get("value");
		if (value == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Missing value.");
		}
		boolean enabled = value.equalsIgnoreCase("true");
		return handleAllowedRequestWithBackendAndValue(exchange, enabled);
	}

	protected abstract Void handleAllowedRequestWithBackendAndValue(HttpAsyncExchange exchange, boolean value) throws IOException;
}
