package core.webui.server.handlers.internals.menu;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.webcommon.HttpServerUtilities;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;

public class MenuSetCompilerPathActionHandler extends AbstractSingleMethodHttpHandler {

	public MenuSetCompilerPathActionHandler() {
		super(AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
		Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(request);
		if (params == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Failed to parse POST parameters.");
		}

		String path = params.get("path");
		if (path == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Path must be provided.");
		}

		if (!backEndHolder.getCompiler().canSetPath()) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Path cannot be set for current compiler.");
		}

		if (!backEndHolder.getCompiler().setPath(new File(path))) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Cannot set path '" + path + "' for current compiler.");
		}

		return HttpServerUtilities.prepareHttpResponse(exchange, 200, "");
	}
}
