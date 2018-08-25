package core.webui.server.handlers.internals.tasks;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.webcommon.HttpServerUtilities;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import utilities.NumberUtility;

public class SetSelectedTaskHandler extends AbstractSingleMethodHttpHandler {

	public SetSelectedTaskHandler() {
		super(AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
			throws HttpException, IOException {
		Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(request);
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
		backEndHolder.setCompilingLanguage(backEndHolder.getCurrentTaskGroup().getTask(index).getCompiler());

		return HttpServerUtilities.prepareTextResponse(exchange, 200, source);
	}
}
