package core.webui.server.handlers.internals.recordsreplays;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;

public class GetIsRecordingHandler extends AbstractSingleMethodHttpHandler {

	public GetIsRecordingHandler() {
		super(AbstractSingleMethodHttpHandler.GET_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
			throws HttpException, IOException {
		boolean response = backEndHolder.isRecording();
		return HttpServerUtilities.prepareHttpResponse(exchange, 200, "" + response);
	}
}
