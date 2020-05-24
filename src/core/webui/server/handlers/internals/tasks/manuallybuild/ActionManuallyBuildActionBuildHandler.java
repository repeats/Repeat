package core.webui.server.handlers.internals.tasks.manuallybuild;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.languageHandler.Language;
import core.userDefinedTask.manualBuild.ManuallyBuildActionConstructor;
import core.userDefinedTask.manualBuild.ManuallyBuildActionConstructorManager;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;

public class ActionManuallyBuildActionBuildHandler extends AbstractSingleMethodHttpHandler {

	private ManuallyBuildActionConstructorManager manuallyBuildActionConstructorManager;

	public ActionManuallyBuildActionBuildHandler(ManuallyBuildActionConstructorManager manuallyBuildActionConstructorManager) {
		super(AbstractSingleMethodHttpHandler.POST_METHOD);
		this.manuallyBuildActionConstructorManager = manuallyBuildActionConstructorManager;
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
		Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(request);
		if (params == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Failed to get POST parameters.");
		}

		String id = params.get("id");
		if (id == null || id.isEmpty()) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "No builder ID provided.");
		}

		ManuallyBuildActionConstructor constructor = manuallyBuildActionConstructorManager.get(id);
		if (constructor == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "No constructor with ID " + id + ".");
		}
		if (backEndHolder.getSelectedLanguage() != Language.MANUAL_BUILD) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Compiling language must be set to manual build first.");
		}

		String source = constructor.generateSource();
		if (!backEndHolder.compileSourceAndSetCurrent(source, null)) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Failed to compile source.");
		}

		return HttpServerUtilities.prepareTextResponse(exchange, 200, "");
	}
}
