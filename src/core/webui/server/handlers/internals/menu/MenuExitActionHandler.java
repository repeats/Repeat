package core.webui.server.handlers.internals.menu;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.webcommon.HttpServerUtilities;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;

public class MenuExitActionHandler extends AbstractSingleMethodHttpHandler {

	private static final long EXIT_DELAY_MS = 2000;

	public MenuExitActionHandler() {
		super(AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				backEndHolder.exit();
			}}, EXIT_DELAY_MS);

		return HttpServerUtilities.prepareHttpResponse(exchange, 200, "Exiting after " + EXIT_DELAY_MS + "ms...");
	}
}
