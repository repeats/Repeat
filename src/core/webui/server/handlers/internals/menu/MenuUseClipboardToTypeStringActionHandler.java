package core.webui.server.handlers.internals.menu;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.http.nio.protocol.HttpAsyncExchange;

import core.webui.server.handlers.AbstractBooleanConfigHttpHandler;
import core.webui.webcommon.HttpServerUtilities;
import utilities.OSIdentifier;

public class MenuUseClipboardToTypeStringActionHandler extends AbstractBooleanConfigHttpHandler {

	private static final Logger LOGGER = Logger.getLogger(MenuUseClipboardToTypeStringActionHandler.class.getName());

	@Override
	protected Void handleAllowedRequestWithBackendAndValue(HttpAsyncExchange exchange, boolean value) throws IOException {
		if (value && OSIdentifier.IS_LINUX) {
			LOGGER.warning("Using clipboard to type string is not supported in Linux. This config will not have any effect.");
		}
		backEndHolder.getConfig().setUseClipboardToTypeString(value);
		return HttpServerUtilities.prepareHttpResponse(exchange, 200, "");
	}
}
