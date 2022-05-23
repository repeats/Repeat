package core.webui.server.handlers.internals.tasks;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.userDefinedTask.UserDefinedAction;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;

public class GetTaskSourceHandler extends AbstractSingleMethodHttpHandler {

	public GetTaskSourceHandler() {
		super(AbstractSingleMethodHttpHandler.GET_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
			throws HttpException, IOException {
		String uriString = request.getRequestLine().getUri();
		Map<String, String>  params = HttpServerUtilities.parseGetParameters(uriString);
		if (params == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Failed to parse URL " + uriString);
		}

		String id = params.get("id");
		if (id == null || id.isEmpty()) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Task ID is empty or not provided.");
		}

		String timestampString = params.get("timestamp");
		if (timestampString == null || timestampString.isEmpty()) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Timestamp is empty or not provided.");
		}

		UserDefinedAction action = backEndHolder.getTask(id);
		if (action == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 404, "No action for ID " + id + ".");
		}

		Long timestamp = Long.parseLong(timestampString);
		String sourceCode = backEndHolder.getSourceForTask(action, timestamp);
		if (sourceCode == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 500, "No source code found for task " + id + ".");
		}

		return HttpServerUtilities.prepareTextResponse(exchange, 200, sourceCode);
	}
}
