package core.webui.server.handlers.internals;

import java.awt.Point;
import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.controller.Core;
import core.webcommon.HttpServerUtilities;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;

public class GetMousePositionHandler extends AbstractSingleMethodHttpHandler {

	public GetMousePositionHandler() {
		super(AbstractSingleMethodHttpHandler.GET_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
			throws HttpException, IOException {
		Point p = Core.getInstance(backEndHolder.getConfig()).mouse().getPosition();
		return HttpServerUtilities.prepareTextResponse(exchange, 200, p.x + ", " + p.y);
	}
}
