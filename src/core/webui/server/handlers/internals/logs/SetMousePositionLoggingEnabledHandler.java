package core.webui.server.handlers.internals.logs;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;

public class SetMousePositionLoggingEnabledHandler extends AbstractSingleMethodHttpHandler {

	public SetMousePositionLoggingEnabledHandler() {
		super(AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
			throws HttpException, IOException {
		Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(request);
		if (params == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Failed to get POST parameters.");
		}

		if (!params.containsKey("enabled")) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Missing required 'enabled' parameter.");
		}
		String enabledString = params.get("enabled");
		boolean enabled = enabledString.equals("" + true);

		backEndHolder.setEnabledMousePositionLogging(enabled);
		return HttpServerUtilities.prepareHttpResponse(exchange, 200, "");
	}
}
