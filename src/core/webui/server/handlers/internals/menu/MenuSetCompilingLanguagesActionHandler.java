package core.webui.server.handlers.internals.menu;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.languageHandler.Language;
import core.languageHandler.sourceGenerator.AbstractSourceGenerator;
import core.webcommon.HttpServerUtilities;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import utilities.NumberUtility;

public class MenuSetCompilingLanguagesActionHandler extends AbstractUIHttpHandler {

	public MenuSetCompilingLanguagesActionHandler(ObjectRenderer objectRenderer) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
		Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(request);
		if (params == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Failed to parse POST parameters.");
		}
		String indexString = params.get("index");
		if (indexString == null || !NumberUtility.isNonNegativeInteger(indexString)) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Index must be provided as non-negative integer.");
		}
		Language language = Language.identify(Integer.parseInt(indexString));
		if (language == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Language index " + indexString + " unknown.");
		}

		backEndHolder.setCompilingLanguage(language);
		String source = AbstractSourceGenerator.getReferenceSource(backEndHolder.getSelectedLanguage());
		return HttpServerUtilities.prepareTextResponse(exchange, 200, source);
	}
}
