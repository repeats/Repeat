package core.webui.server.handlers.internals.tasks.manuallybuild;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import argo.jdom.JsonNode;
import core.userDefinedTask.manualBuild.ManuallyBuildActionConstructor;
import core.userDefinedTask.manualBuild.ManuallyBuildActionConstructorManager;
import core.userDefinedTask.manualBuild.ManuallyBuildStep;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.server.handlers.renderedobjects.RenderedManuallyBuildSteps;
import core.webui.webcommon.HttpServerUtilities;
import utilities.ExceptionsUtility;

public class ActionManuallyBuildActionAddStepHandler extends AbstractUIHttpHandler {

	protected ManuallyBuildActionConstructorManager manuallyBuildActionConstructorManager;

	public ActionManuallyBuildActionAddStepHandler(ObjectRenderer objectRenderer, ManuallyBuildActionConstructorManager manuallyBuildActionConstructorManager) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.POST_METHOD);
		this.manuallyBuildActionConstructorManager = manuallyBuildActionConstructorManager;
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
		JsonNode params = HttpServerUtilities.parsePostParameters(request);
		if (params == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Failed to get POST parameters.");
		}

		if (!params.isStringValue("id")) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "No builder ID provided.");
		}

		String id = params.getStringValue("id");
		if (id == null || id.isEmpty()) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "No builder ID provided.");
		}

		ManuallyBuildActionConstructor constructor = manuallyBuildActionConstructorManager.get(id);
		if (constructor == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "No builder for ID " + id + ".");
		}

		ManuallyBuildStep step = null;
		try {
			step = getStepFromRequest(params);
		} catch (InvalidManuallyBuildComponentException e) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, ExceptionsUtility.getStackTrace(e));
		}
		if (step == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Cannot parse step.");
		}

		constructor.addStep(step);

		Map<String, Object> data = new HashMap<>();
		data.put("constructor", RenderedManuallyBuildSteps.fromManuallyBuildActionConstructor(constructor));
		String page = objectRenderer.render("fragments/task_builder_steps_table_rendered", data);
		if (page == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Failed to render page.");
		}
		return HttpServerUtilities.prepareHttpResponse(exchange, HttpStatus.SC_OK, page);
	}

	private ManuallyBuildStep getStepFromRequest(JsonNode params) throws InvalidManuallyBuildComponentException {
		if (!params.isStringValue("actor")) {
			throw new InvalidManuallyBuildComponentException("Paramter 'actor' must be provided.");
		}
		if (!params.isStringValue("action")) {
			throw new InvalidManuallyBuildComponentException("Paramter 'action' must be provided.");
		}
		if (!params.isStringValue("parameters")) {
			throw new InvalidManuallyBuildComponentException("Paramter 'parameters' must be provided.");
		}

		String actor = params.getStringValue("actor").toLowerCase();
		String action = params.getStringValue("action").toLowerCase();
		String parameters = params.getStringValue("parameters");
		return ManuallyBuildActionParametersParser.of().parse(actor, action, parameters);
	}
}
