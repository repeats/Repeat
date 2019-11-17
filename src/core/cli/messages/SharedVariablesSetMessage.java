package core.cli.messages;

import argo.jdom.JsonNode;
import utilities.json.AutoJsonable;
import utilities.json.Jsonizer;

public class SharedVariablesSetMessage extends AutoJsonable {

	private String namespace;
	private String variable;
	private String value;

	public static SharedVariablesSetMessage parseJSON(JsonNode node) {
		SharedVariablesSetMessage output = new SharedVariablesSetMessage();
		return Jsonizer.parse(node, output) ? output : null;
	}

	public static SharedVariablesSetMessage of() {
		return new SharedVariablesSetMessage();
	}

	public String getNamespace() {
		return namespace;
	}

	public String getVariable() {
		return variable;
	}

	public String getValue() {
		return value;
	}

	public SharedVariablesSetMessage setNamespace(String namespace) {
		this.namespace = namespace;
		return this;
	}

	public SharedVariablesSetMessage setVariable(String variable) {
		this.variable = variable;
		return this;
	}

	public SharedVariablesSetMessage setValue(String value) {
		this.value = value;
		return this;
	}
}
