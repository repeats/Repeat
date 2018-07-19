package core.webui.server.handlers.internals.taskcreation;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.webcommon.HttpServerUtilities;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;

public class ActionStopRunningCompiledTaskHandler extends AbstractSingleMethodHttpHandler {

	public ActionStopRunningCompiledTaskHandler() {
		super(AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
			throws HttpException, IOException {
		backEndHolder.stopRunningCompiledAction();
		return HttpServerUtilities.prepareTextResponse(exchange, 200, "");
	}
}
