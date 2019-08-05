package core.controller;

import java.util.List;
import java.util.stream.Collectors;

import argo.jdom.JsonNode;
import core.config.AbstractRemoteRepeatsClientsConfig;

public class CoreConfig extends AbstractRemoteRepeatsClientsConfig {

	public CoreConfig(List<String> remoteClientIds) {
		super(remoteClientIds);
	}

	public static CoreConfig parseJSON(JsonNode node) {
		return new CoreConfig(node.getArrayNode().stream().map(n -> n.getStringValue()).collect(Collectors.toList()));
	}
}
