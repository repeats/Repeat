package core.webui.server.handlers.internals.menu;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.webcommon.HttpServerUtilities;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;

public class MenuSaveConfigActionHandler extends AbstractSingleMethodHttpHandler {

	public MenuSaveConfigActionHandler() {
		super(AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
		if (!backEndHolder.getConfig().writeConfig()) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Unable to save config...");
		}
		return HttpServerUtilities.prepareHttpResponse(exchange, 200, "");
	}
}
