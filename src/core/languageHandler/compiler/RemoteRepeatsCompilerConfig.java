package core.languageHandler.compiler;

import java.util.ArrayList;
import java.util.List;

import argo.jdom.JsonNode;
import core.config.AbstractRemoteRepeatsClientsConfig;

public class RemoteRepeatsCompilerConfig extends AbstractRemoteRepeatsClientsConfig {
	protected RemoteRepeatsCompilerConfig(List<String> remoteClientIds) {
		super(remoteClientIds);
	}

	@Override
	public RemoteRepeatsCompilerConfig clone() {
		return new RemoteRepeatsCompilerConfig(new ArrayList<>(getClients()));
	}

	public static RemoteRepeatsCompilerConfig parseJSON(JsonNode node) {
		return new RemoteRepeatsCompilerConfig(AbstractRemoteRepeatsClientsConfig.parseClientList(node));
	}
}
