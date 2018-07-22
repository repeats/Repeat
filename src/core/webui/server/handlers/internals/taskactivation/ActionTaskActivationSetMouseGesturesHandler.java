package core.webui.server.handlers.internals.taskactivation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpException;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import argo.jdom.JsonNode;
import core.keyChain.MouseGesture;
import core.keyChain.TaskActivationConstructor;
import core.keyChain.TaskActivationConstructorManager;
import core.webcommon.HttpServerUtilities;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.server.handlers.renderedobjects.RenderedMouseGestureActivation;
import utilities.json.JSONUtility;

public class ActionTaskActivationSetMouseGesturesHandler extends AbstractTaskActivationConstructorActionHandler {

	public ActionTaskActivationSetMouseGesturesHandler(ObjectRenderer objectRenderer,
			TaskActivationConstructorManager taskActivationConstructorManager) {
		super(objectRenderer, taskActivationConstructorManager);
	}

	@Override
	protected Void handleRequestWithBackendAndConstructor(HttpAsyncExchange exchange,
			TaskActivationConstructor constructor, Map<String, String> params) throws HttpException, IOException {
		String nodeString = params.get("gestures");
		if (nodeString == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "List of gesture indices must be provided.");
		}

		JsonNode node = JSONUtility.jsonFromString(nodeString);
		if (node == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Failed to parse list of gesture indices as JSON.");
		}
		if (!node.isArrayNode()) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "List of gesture indices must be a list.");
		}
		List<Integer> indices = new ArrayList<>();
		for (JsonNode index : node.getNullableArrayNode()) {
			if (!index.isNumberValue()) {
				return HttpServerUtilities.prepareHttpResponse(exchange, 400, "All gesture indices must be numbers.");
			}
			indices.add(Integer.parseInt(index.getNumberValue()));
		}

		MouseGesture[] gestures = RenderedMouseGestureActivation.INDICES;
		Set<MouseGesture> chosenGestures = new HashSet<>();
		for (int i : indices) {
			if (i < 0 || i >= gestures.length) {
				return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Gesture index out of bound: " + i + ".");
			}
			chosenGestures.add(gestures[i]);
		}

		constructor.setMouseGestures(chosenGestures);
		return HttpServerUtilities.prepareHttpResponse(exchange, 200, "");
	}
}
