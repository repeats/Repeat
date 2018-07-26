package core.webui.server.handlers.internals.menu;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.languageHandler.Language;
import core.languageHandler.compiler.JavaNativeCompiler;
import core.webcommon.HttpServerUtilities;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import utilities.StringUtilities;

public class MenuGetCompilerConfigOptionActionHandler extends AbstractUIHttpHandler {

	public MenuGetCompilerConfigOptionActionHandler(ObjectRenderer objectRenderer) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.GET_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
		Language language = backEndHolder.getSelectedLanguage();
		if (language != Language.JAVA) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Current language " + language.name() + " does not support changing configuration.");
		}

		JavaNativeCompiler compiler = (JavaNativeCompiler) backEndHolder.getCompiler();
		Map<String, Object> data = new HashMap<>();
		data.put("classPaths", StringUtilities.join(compiler.getClassPath(), "\n"));
		return renderedPage(exchange, "fragments/java_compiler_configuration", data);
	}
}
