package core.webui.server.handlers.internals.menu;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.webcommon.HttpServerUtilities;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.renderedobjects.RenderedDebugLevel;
import utilities.NumberUtility;

public class MenuSetDebugLevelActionHandler extends AbstractSingleMethodHttpHandler {

	public MenuSetDebugLevelActionHandler() {
		super(AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
		Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(request);
		if (params == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Failed to parse POST parameters.");
		}

		String levelString = params.get("index");
		if (levelString == null || !NumberUtility.isNonNegativeInteger(levelString)) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Level must be non-negative integer.");
		}

		int levelIndex = Integer.parseInt(levelString);
		if (levelIndex >= RenderedDebugLevel.LEVELS.length) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Index " + levelIndex + " out of bound.");
		}
		Level level = RenderedDebugLevel.LEVELS[levelIndex];
		backEndHolder.changeDebugLevel(level);
		return emptySuccessResponse(exchange);
	}
}
