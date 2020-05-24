package core.webui.server.handlers.internals.tasks.manuallybuild;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.userDefinedTask.manualBuild.ManuallyBuildActionConstructor;
import core.userDefinedTask.manualBuild.ManuallyBuildActionConstructorManager;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.server.handlers.renderedobjects.RenderedManuallyBuildSteps;
import core.webui.webcommon.HttpServerUtilities;
import utilities.NumberUtility;

public class ActionManuallyBuildActionRemoveStepHandler extends AbstractUIHttpHandler {

	protected ManuallyBuildActionConstructorManager manuallyBuildActionConstructorManager;

	public ActionManuallyBuildActionRemoveStepHandler(ObjectRenderer objectRenderer, ManuallyBuildActionConstructorManager manuallyBuildActionConstructorManager) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.POST_METHOD);

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

		String indexString = params.get("index");
		if (!NumberUtility.isNonNegativeInteger(indexString)) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Index must be a non-negative integer but got " + indexString + ".");
		}

		ManuallyBuildActionConstructor constructor = manuallyBuildActionConstructorManager.get(id);
		constructor.removeStep(Integer.parseInt(indexString));

		Map<String, Object> data = new HashMap<>();
		data.put("constructor", RenderedManuallyBuildSteps.fromManuallyBuildActionConstructor(constructor));
		String page = objectRenderer.render("fragments/task_builder_steps_table_rendered", data);
		if (page == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Failed to render page.");
		}
		return HttpServerUtilities.prepareHttpResponse(exchange, HttpStatus.SC_OK, page);
	}

}
