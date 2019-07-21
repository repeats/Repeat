package core.webui.server.handlers.internals;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;
import utilities.NumberUtility;

public class GetLogsHandler extends AbstractSingleMethodHttpHandler {

	public GetLogsHandler() {
		super(AbstractSingleMethodHttpHandler.GET_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
			throws HttpException, IOException {
		Map<String, String> params = HttpServerUtilities.parseGetParameters(request.getRequestLine().getUri());
		if (params == null) {
			return HttpServerUtilities.prepareTextResponse(exchange, 400, "Unable to parse GET parameters.");
		}

		String since = params.get("since");
		if (since == null) {
			return HttpServerUtilities.prepareTextResponse(exchange, 400, "Since time must be provided.");
		}

		if (!NumberUtility.isNonNegativeInteger(since)) {
			return HttpServerUtilities.prepareTextResponse(exchange, 400, "Since time must be non-negative integer.");
		}

		long time = Long.parseLong(since);
		return HttpServerUtilities.prepareTextResponse(exchange, 200, backEndHolder.getLogsSince(time));
	}
}
