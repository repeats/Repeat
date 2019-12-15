package core.userDefinedTask.internals;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import utilities.json.IJsonable;

public class SharedVariablesSubscription implements IJsonable {

	private boolean all;
	private boolean allForNamespace;
	private String namespace;
	private String name;

	private SharedVariablesSubscription(String namespace, String name, boolean all, boolean allForNamespace) {
		this.namespace = namespace;
		this.name = name;
		this.all = all;
		this.allForNamespace = allForNamespace;
	}

	public static SharedVariablesSubscription forAll() {
		return new SharedVariablesSubscription(null, null, true, true);
	}

	public static SharedVariablesSubscription forNamespace(String namespace) {
		return new SharedVariablesSubscription(namespace, null, false, true);
	}

	public static SharedVariablesSubscription forVar(String namespace, String name) {
		return new SharedVariablesSubscription(namespace, name, false, false);
	}

	public boolean isAll() {
		return all;
	}

	public boolean isAllForNamespace() {
		return allForNamespace;
	}

	public String getNamespace() {
		return namespace;
	}

	public String getName() {
		return name;
	}

	public boolean includes(SharedVariablesEvent e) {
		if (all) {
			return true;
		}

		if (!e.getNamespace().equals(namespace)) {
			return false;
		}
		if (allForNamespace) {
			return true;
		}

		return e.getName().equals(name);
	}

	@Override
	public JsonRootNode jsonize() {
		return JsonNodeFactories.object(
				JsonNodeFactories.field("all", JsonNodeFactories.booleanNode(all)),
				JsonNodeFactories.field("all_for_namespace", JsonNodeFactories.booleanNode(allForNamespace)),
				JsonNodeFactories.field("namespace", JsonNodeFactories.string(namespace)),
				JsonNodeFactories.field("name", JsonNodeFactories.string(name))
				);
	}

	public static SharedVariablesSubscription parseJSON(JsonNode node) {
		boolean all = node.getBooleanValue("all");
		boolean allForNamespace = node.getBooleanValue("all_for_namespace");
		String namespace = node.getStringValue("namespace");
		String name = node.getStringValue("name");
		return new SharedVariablesSubscription(namespace, name, all, allForNamespace);
	}
}
