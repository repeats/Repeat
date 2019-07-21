package core.webui.server.handlers.internals.taskcreation;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.languageHandler.sourceGenerator.AbstractSourceGenerator;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;

public class GetSourceTemplateHandler extends AbstractSingleMethodHttpHandler {

	public GetSourceTemplateHandler() {
		super(AbstractSingleMethodHttpHandler.GET_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
			throws HttpException, IOException {
		String source = AbstractSourceGenerator.getReferenceSource(backEndHolder.getSelectedLanguage());
		return HttpServerUtilities.prepareTextResponse(exchange, 200, source);
	}
}
