package core.webui.server.handlers.internals.menu;

import java.io.IOException;

import org.apache.http.nio.protocol.HttpAsyncExchange;

import core.webcommon.HttpServerUtilities;
import core.webui.server.handlers.AbstractBooleanConfigHttpHandler;

public class MenuUseClipboardToTypeStringActionHandler extends AbstractBooleanConfigHttpHandler {

	@Override
	protected Void handleAllowedRequestWithBackendAndValue(HttpAsyncExchange exchange, boolean value) throws IOException {
		backEndHolder.getConfig().setUseClipboardToTypeString(value);
		return HttpServerUtilities.prepareHttpResponse(exchange, 200, "");
	}
}
