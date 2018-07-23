package core.webui.server.handlers.internals.menu;

import java.io.IOException;

import org.apache.http.nio.protocol.HttpAsyncExchange;

import core.webcommon.HttpServerUtilities;
import core.webui.server.handlers.AbstractBooleanConfigHttpHandler;

public class MenuHaltTaskByEscapeActionHandler extends AbstractBooleanConfigHttpHandler {

	@Override
	protected Void handleAllowedRequestWithBackendAndValue(HttpAsyncExchange exchange, boolean value) throws IOException {
		backEndHolder.getConfig().setEnabledHaltingKeyPressed(value);
		return HttpServerUtilities.prepareHttpResponse(exchange, 200, "");
	}
}
