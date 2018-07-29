package core.webui.server.handlers.internals.taskcreation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.webcommon.HttpServerUtilities;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;

public class ActionEditSourceHandler extends AbstractSingleMethodHttpHandler {

	public ActionEditSourceHandler() {
		super(AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
			throws HttpException, IOException {
		byte[] content = HttpServerUtilities.getPostContent(request);
		if (content == null) {
			return HttpServerUtilities.prepareTextResponse(exchange, 500, "Failed to get POST content.");
		}

		String source = new String(content, StandardCharsets.UTF_8);
		backEndHolder.editSourceCode(source);
		return HttpServerUtilities.prepareTextResponse(exchange, 200, "");
	}
}
