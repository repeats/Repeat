package core.webui.server.handlers.internals.tasks;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;
import utilities.NumberUtility;

public class GetSourceForTaskHandler extends AbstractSingleMethodHttpHandler {

	public GetSourceForTaskHandler() {
		super(AbstractSingleMethodHttpHandler.GET_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
			throws HttpException, IOException {
		Map<String, String> params = HttpServerUtilities.parseGetParameters(request.getRequestLine().getUri());
		if (params == null) {
			return HttpServerUtilities.prepareTextResponse(exchange, 500, "Unable to parse GET request parameters.");
		}
		String indexString = params.get("task");
		if (indexString == null || !NumberUtility.isNonNegativeInteger(indexString)) {
			return HttpServerUtilities.prepareTextResponse(exchange, 400, "Task index must be provided as a non-negative integer.");
		}
		int index = Integer.parseInt(indexString);

		String source = backEndHolder.getSource(index);
		if (source == null) {
			return HttpServerUtilities.prepareTextResponse(exchange, 500, "Unable to get source code.");
		}

		return HttpServerUtilities.prepareTextResponse(exchange, 200, source);
	}
}
