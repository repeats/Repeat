package core.webui.server.handlers.internals.menu;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.webcommon.HttpServerUtilities;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;

public class MenuGetGeneratedSourceHandler extends AbstractSingleMethodHttpHandler {

	public MenuGetGeneratedSourceHandler() {
		super(AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
		String source = backEndHolder.generateSource();
		if (source == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Unable to generate source code.");
		}

		return HttpServerUtilities.prepareTextResponse(exchange, 200, source);
	}
}
