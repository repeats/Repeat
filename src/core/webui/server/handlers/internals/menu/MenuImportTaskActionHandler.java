package core.webui.server.handlers.internals.menu;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.webcommon.HttpServerUtilities;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;

public class MenuImportTaskActionHandler extends AbstractSingleMethodHttpHandler {

	public MenuImportTaskActionHandler() {
		super(AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
		Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(request);
		if (params == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Failed to get POST parameters.");
		}
		String path = params.get("path");
		if (path == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Path must be provided.");
		}
		if (!Files.isRegularFile(Paths.get(path))) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Path '" + path + "' is not a file.");
		}

		backEndHolder.importTasks(new File(path));
		return emptySuccessResponse(exchange);
	}
}
