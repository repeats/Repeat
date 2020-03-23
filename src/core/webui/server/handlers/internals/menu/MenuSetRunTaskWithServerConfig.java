package core.webui.server.handlers.internals.menu;

import java.io.IOException;

import org.apache.http.nio.protocol.HttpAsyncExchange;

import core.webui.server.handlers.AbstractBooleanConfigHttpHandler;
import core.webui.webcommon.HttpServerUtilities;

public class MenuSetRunTaskWithServerConfig extends AbstractBooleanConfigHttpHandler {

	@Override
	protected Void handleAllowedRequestWithBackendAndValue(HttpAsyncExchange exchange, boolean value) throws IOException {
		backEndHolder.getConfig().setRunTaskWithServerConfig(value);
		return HttpServerUtilities.prepareHttpResponse(exchange, 200, "");
	}
}
