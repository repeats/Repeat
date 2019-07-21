package core.webui.server.handlers.internals.recordsreplays;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;

public class ActionStartRecordingHandler extends AbstractSingleMethodHttpHandler {

	public ActionStartRecordingHandler() {
		super(AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
			throws HttpException, IOException {
		backEndHolder.startRecording();
		return HttpServerUtilities.prepareHttpResponse(exchange, 200, "");
	}
}
