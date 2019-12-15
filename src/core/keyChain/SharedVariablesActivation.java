package core.keyChain;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.userDefinedTask.internals.SharedVariablesSubscription;
import utilities.json.IJsonable;

public class SharedVariablesActivation implements IJsonable {
	private SharedVariablesSubscription variable;

	private SharedVariablesActivation(SharedVariablesSubscription variable) {
		this.variable = variable;
	}

	public static SharedVariablesActivation of(SharedVariablesSubscription variable) {
		return new SharedVariablesActivation(variable);
	}

	public SharedVariablesSubscription getVariable() {
		return variable;
	}

	@Override
	public JsonRootNode jsonize() {
		return JsonNodeFactories.object(JsonNodeFactories.field("variable", variable.jsonize()));
	}

	public static SharedVariablesActivation parseJSON(JsonNode node) {
		JsonNode variableNode = node.getNode("variable");
		SharedVariablesSubscription variable = SharedVariablesSubscription.parseJSON(variableNode);
		return of(variable);
	}
}
