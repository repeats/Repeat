package core.userDefinedTask.internals;

import java.util.List;

import argo.jdom.JsonNode;
import core.config.AbstractRemoteRepeatsClientsConfig;

public class ToolsConfig extends AbstractRemoteRepeatsClientsConfig {

	public ToolsConfig(List<String> remoteClientIds) {
		super(remoteClientIds);
	}

	public static ToolsConfig parseJSON(JsonNode node) {
		return new ToolsConfig(AbstractRemoteRepeatsClientsConfig.parseClientList(node));
	}
}
