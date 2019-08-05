package core.userDefinedTask.internals;

import java.util.List;
import java.util.stream.Collectors;

import argo.jdom.JsonNode;
import core.config.AbstractRemoteRepeatsClientsConfig;

public class ToolsConfig extends AbstractRemoteRepeatsClientsConfig {

	public ToolsConfig(List<String> remoteClientIds) {
		super(remoteClientIds);
	}

	public static ToolsConfig parseJSON(JsonNode node) {
		return new ToolsConfig(node.getArrayNode().stream().map(n -> n.getStringValue()).collect(Collectors.toList()));
	}
}
