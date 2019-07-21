package core.webui.server.handlers.internals.menu;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.languageHandler.Language;
import core.languageHandler.compiler.JavaNativeCompiler;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;

public class MenuSetCompilerConfigActionHandler extends AbstractSingleMethodHttpHandler {

	public MenuSetCompilerConfigActionHandler() {
		super(AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
		Language language = backEndHolder.getSelectedLanguage();
		if (language != Language.JAVA) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Current language " + language.name() + " does not support changing configuration.");
		}

		Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(request);
		if (params == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Failed to get POST parameters.");
		}
		String allClassPaths = params.get("classPaths");
		if (allClassPaths == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Class paths must be provided.");
		}
		String[] classPaths = allClassPaths.split("\n");

		JavaNativeCompiler compiler = (JavaNativeCompiler) backEndHolder.getCompiler();
		if (!compiler.setClassPath(Arrays.asList(classPaths))) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Failed to set class paths.");
		}
		return emptySuccessResponse(exchange);
	}
}
