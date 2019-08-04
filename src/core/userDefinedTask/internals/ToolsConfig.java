package core.userDefinedTask.internals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import utilities.json.IJsonable;
import utilities.json.JSONUtility;

public class ToolsConfig implements IJsonable {

	public static final String LOCAL_CLIENT = "local";

	private List<String> enabledClients;

	public ToolsConfig(List<String> remoteClientIds) {
		this.enabledClients = new ArrayList<>(remoteClientIds);
	}

	public List<String> getClients() {
		return enabledClients;
	}

	public void setClients(List<String> remoteClientIds) {
		enabledClients.clear();
		enabledClients.addAll(remoteClientIds);
	}

	@Override
	public JsonRootNode jsonize() {
		return JsonNodeFactories.array(JSONUtility.listToJson(enabledClients));
	}

	public static ToolsConfig parseJSON(JsonNode node) {
		return new ToolsConfig(node.getArrayNode().stream().map(n -> n.getStringValue()).collect(Collectors.toList()));
	}
}
