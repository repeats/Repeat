package core.webui.server.handlers.internals.taskactivation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import argo.jdom.JsonNode;
import core.keyChain.SharedVariablesActivation;
import core.keyChain.TaskActivationConstructor;
import core.keyChain.TaskActivationConstructorManager;
import core.userDefinedTask.internals.SharedVariablesSubscription;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;
import utilities.json.JSONUtility;
import utilities.json.Jsonizer;

public class ActionTaskActivationAddSharedVariables extends AbstractTaskActivationConstructorActionHandler {

	public static final String ALL = "ALL";

	public ActionTaskActivationAddSharedVariables(ObjectRenderer objectRenderer, TaskActivationConstructorManager taskActivationConstructorManager) {
		super(objectRenderer, taskActivationConstructorManager);
	}

	@Override
	protected Void handleRequestWithBackendAndConstructor(HttpAsyncExchange exchange, TaskActivationConstructor constructor, Map<String, String> params) throws HttpException, IOException {
		if (!params.containsKey("variables")) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Request missing contain list of variables.");
		}
		String variables = params.get("variables");
		JsonNode node = JSONUtility.jsonFromString(variables);
		if (node == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Request missing list of variables is not a valid JSON.");
		}

		SharedVariableListClientRequest variableList = new SharedVariableListClientRequest();
		if (!Jsonizer.parse(node, variableList)) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Request list of variables cannot be parsed.");
		}

		List<SharedVariablesActivation> activationList = new ArrayList<>();
		for (SharedVariableClientRequest request : variableList.getVars()) {
			String namespace = request.getNamespace();
			String name = request.getName();
			SharedVariablesActivation sharedVariablesActivation;
			if (namespace.equals(ALL)) {
				sharedVariablesActivation = SharedVariablesActivation.of(SharedVariablesSubscription.forAll());
			} else if (name.equals(ALL)) {
				sharedVariablesActivation = SharedVariablesActivation.of(SharedVariablesSubscription.forNamespace(namespace));
			} else {
				sharedVariablesActivation = SharedVariablesActivation.of(SharedVariablesSubscription.forVar(namespace, name));
			}

			activationList.add(sharedVariablesActivation);
		}
		constructor.addSharedVariables(activationList);
		return renderedTaskActivationPage(exchange, "fragments/shared_variables", constructor);
	}

}
