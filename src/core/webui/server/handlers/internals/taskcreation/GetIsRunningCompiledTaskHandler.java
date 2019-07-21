package core.webui.server.handlers.internals.taskcreation;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;

public class GetIsRunningCompiledTaskHandler extends AbstractSingleMethodHttpHandler {

	public GetIsRunningCompiledTaskHandler() {
		super(AbstractSingleMethodHttpHandler.GET_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
			throws HttpException, IOException {
		boolean response = backEndHolder.isRunningCompiledAction();
		return HttpServerUtilities.prepareTextResponse(exchange, 200, response + "");
	}
}
