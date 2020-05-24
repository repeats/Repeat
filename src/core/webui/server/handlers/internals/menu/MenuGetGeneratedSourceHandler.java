package core.webui.server.handlers.internals.menu;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import argo.jdom.JsonNode;
import core.languageHandler.Language;
import core.languageHandler.compiler.DynamicCompilationResult;
import core.languageHandler.compiler.DynamicCompilerOutput;
import core.userDefinedTask.UserDefinedAction;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractTaskSourceCodeHandler;
import core.webui.server.handlers.internals.tasks.TaskSourceCodeFragmentHandler;
import core.webui.server.handlers.internals.tasks.TaskSourceCodeFragmentHandler.RenderException;
import core.webui.webcommon.HttpServerUtilities;

public class MenuGetGeneratedSourceHandler extends AbstractTaskSourceCodeHandler {

	public MenuGetGeneratedSourceHandler(TaskSourceCodeFragmentHandler taskSourceCodeFragmentHandler) {
		super(taskSourceCodeFragmentHandler, AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
		String source = backEndHolder.generateSource();
		if (source == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Unable to generate source code.");
		}

		Language language = backEndHolder.getSelectedLanguage();
		UserDefinedAction action = null;
		if (language == Language.MANUAL_BUILD) {
			DynamicCompilationResult compilationResult = backEndHolder.getCompiler().compile(source);
			if (compilationResult.output() != DynamicCompilerOutput.COMPILATION_SUCCESS) {
				return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Unable to compile generated source code.");
			}

			action = compilationResult.action();
		}
		try {
			JsonNode data = taskSourceCodeFragmentHandler.render(language, source, action);
			return HttpServerUtilities.prepareJsonResponse(exchange, 200, data);
		} catch (RenderException e) {
			return HttpServerUtilities.prepareTextResponse(exchange, 500, "Failed to render page: " + e.getMessage());
		}
	}
}
