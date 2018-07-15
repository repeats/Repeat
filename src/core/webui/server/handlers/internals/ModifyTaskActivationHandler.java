package core.webui.server.handlers.internals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import argo.jdom.JsonNode;
import core.keyChain.TaskActivation;
import core.userDefinedTask.UserDefinedAction;
import core.webcommon.HttpServerUtilities;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.CommonTask;
import utilities.json.JSONUtility;

public class ModifyTaskActivationHandler extends AbstractSingleMethodHttpHandler {

	public ModifyTaskActivationHandler() {
		super(AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
			throws HttpException, IOException {
		byte[] content = HttpServerUtilities.getPostContent(request);
		if (content == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Failed to get POST content.");
		}
		String contentString = new String(content, StandardCharsets.UTF_8);
		JsonNode node = JSONUtility.jsonFromString(contentString);
		if (node == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Failed to get JSON content.");
		}
		if (!node.isObjectNode("activation")) { // When no activation provided, redirect to home page.
			return HttpServerUtilities.redirect(exchange, "/");
		}

		Map<String, String> params = new HashMap<>();
		if (node.isStringValue("group")) {
			params.put("group", node.getStringValue("group"));
		}
		if (node.isStringValue("task")) {
			params.put("task", node.getStringValue("task"));
		}
		UserDefinedAction task = CommonTask.getTaskFromRequest(backEndHolder, params);
		if (task == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 404, "Cannot find task found.");
		}

		TaskActivation activation = TaskActivation.parseJSON(node);
		if (activation == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Cannot parse activation.");
		}

		task.setActivation(activation);
		return HttpServerUtilities.redirect(exchange, "/");
	}
}
