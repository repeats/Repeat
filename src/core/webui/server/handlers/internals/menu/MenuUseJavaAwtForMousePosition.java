package core.webui.server.handlers.internals.menu;

import java.io.IOException;

import org.apache.http.nio.protocol.HttpAsyncExchange;

import core.webui.server.handlers.AbstractBooleanConfigHttpHandler;

public class MenuUseJavaAwtForMousePosition extends AbstractBooleanConfigHttpHandler {

	@Override
	protected Void handleAllowedRequestWithBackendAndValue(HttpAsyncExchange exchange, boolean value) throws IOException {
		backEndHolder.getConfig().setUseJavaAwtToGetMousePosition(value);
		return emptySuccessResponse(exchange);
	}
}
