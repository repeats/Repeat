package core.webui.server.handlers;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.languageHandler.Language;
import core.webcommon.HttpServerUtilities;
import staticResources.BootStrapResources;

public class ApiPageHandler extends AbstractSingleMethodHttpHandler {

	public ApiPageHandler() {
		super(AbstractSingleMethodHttpHandler.GET_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
		Language selected = backEndHolder.getSelectedLanguage();
		return HttpServerUtilities.prepareTextResponse(exchange, 200, BootStrapResources.getAPI(selected));
	}
}
