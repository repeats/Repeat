package core.cli.messages;

import argo.jdom.JsonNode;
import utilities.json.AutoJsonable;
import utilities.json.Jsonizer;

public class SharedVariablesGetMessage extends AutoJsonable {

	private String namespace;
	private String variable;

	public static SharedVariablesGetMessage parseJSON(JsonNode node) {
		SharedVariablesGetMessage output = new SharedVariablesGetMessage();
		return Jsonizer.parse(node, output) ? output : null;
	}

	public static SharedVariablesGetMessage of() {
		return new SharedVariablesGetMessage();
	}

	public String getNamespace() {
		return namespace;
	}

	public String getVariable() {
		return variable;
	}

	public SharedVariablesGetMessage setNamespace(String namespace) {
		this.namespace = namespace;
		return this;
	}

	public SharedVariablesGetMessage setVariable(String variable) {
		this.variable = variable;
		return this;
	}
}
