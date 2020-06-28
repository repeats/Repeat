package core.webui.server.handlers.internals.tasks;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import argo.jdom.JsonNode;
import core.languageHandler.Language;
import core.userDefinedTask.UserDefinedAction;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractTaskSourceCodeHandler;
import core.webui.server.handlers.internals.tasks.TaskSourceCodeFragmentHandler.RenderException;
import core.webui.webcommon.HttpServerUtilities;

public class SetSelectedTaskHandler extends AbstractTaskSourceCodeHandler {

	public SetSelectedTaskHandler(TaskSourceCodeFragmentHandler taskSourceCodeFragmentHandler) {
		super(taskSourceCodeFragmentHandler, AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
		Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(request);
		if (params == null) {
			return HttpServerUtilities.prepareTextResponse(exchange, 500, "Unable to parse GET request parameters.");
		}
		String taskId = params.get("task");
		if (taskId == null || taskId.isEmpty()) {
			return HttpServerUtilities.prepareTextResponse(exchange, 400, "Task ID must be provided.");
		}

		UserDefinedAction action = backEndHolder.getTask(taskId);
		Language language = action.getCompiler();


		try {
			JsonNode data = taskSourceCodeFragmentHandler.render(language, action.getSource(), action);
			backEndHolder.setCompilingLanguage(language);
			return HttpServerUtilities.prepareJsonResponse(exchange, 200, data);
		} catch (RenderException e) {
			return HttpServerUtilities.prepareTextResponse(exchange, 500, "Failed to render page: " + e.getMessage());
		}
	}
}
