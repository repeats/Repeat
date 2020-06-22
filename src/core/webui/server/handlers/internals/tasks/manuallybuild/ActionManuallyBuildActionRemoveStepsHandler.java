package core.webui.server.handlers.internals.tasks.manuallybuild;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import argo.jdom.JsonNode;
import core.userDefinedTask.manualBuild.ManuallyBuildActionConstructor;
import core.userDefinedTask.manualBuild.ManuallyBuildActionConstructorManager;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.server.handlers.renderedobjects.RenderedManuallyBuildSteps;
import core.webui.webcommon.HttpServerUtilities;

public class ActionManuallyBuildActionRemoveStepsHandler extends AbstractUIHttpHandler {

	protected ManuallyBuildActionConstructorManager manuallyBuildActionConstructorManager;

	public ActionManuallyBuildActionRemoveStepsHandler(ObjectRenderer objectRenderer, ManuallyBuildActionConstructorManager manuallyBuildActionConstructorManager) {
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

		if (!params.isArrayNode("indices")) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Indices is not an array node.");
		}

		List<JsonNode> indicesNodes = params.getArrayNode("indices");
		if (indicesNodes.stream().anyMatch(n -> !n.isNumberValue())) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Indices must all be integers.");
		}
		// Get indices, largest one first.
		List<Integer> indices = indicesNodes.stream().map(n -> Integer.parseInt(n.getNumberValue())).sorted((i1, i2) -> Integer.compare(i2, i1)).collect(Collectors.toList());

		ManuallyBuildActionConstructor constructor = manuallyBuildActionConstructorManager.get(id);
		// Since the list of indices is sorted with the largest one first, it's safe to remove them sequentially.
		indices.stream().forEach(i -> constructor.removeStep(i));

		Map<String, Object> data = new HashMap<>();
		data.put("constructor", RenderedManuallyBuildSteps.fromManuallyBuildActionConstructor(constructor));
		String page = objectRenderer.render("fragments/task_builder_steps_table_rendered", data);
		if (page == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Failed to render page.");
		}
		return HttpServerUtilities.prepareHttpResponse(exchange, HttpStatus.SC_OK, page);
	}
}
