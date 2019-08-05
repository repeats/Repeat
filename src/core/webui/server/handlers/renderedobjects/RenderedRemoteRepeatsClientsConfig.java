package core.webui.server.handlers.renderedobjects;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import core.config.AbstractRemoteRepeatsClientsConfig;
import core.ipc.repeatClient.repeatPeerClient.RepeatsPeerServiceClientManager;
import core.userDefinedTask.internals.ToolsConfig;

public class RenderedRemoteRepeatsClientsConfig {

	private List<RenderedConfigRemotePeerClient> clients;

	private RenderedRemoteRepeatsClientsConfig(List<RenderedConfigRemotePeerClient> clients) {
		this.clients = clients;
	}

	public static RenderedRemoteRepeatsClientsConfig of(RepeatsPeerServiceClientManager manager, AbstractRemoteRepeatsClientsConfig config) {
		Set<String> enabledClients = config.getClients().stream().collect(Collectors.toSet());

		List<RenderedConfigRemotePeerClient> clients = Stream.concat(
				Stream.concat(config.getClients().stream(), Stream.of(AbstractRemoteRepeatsClientsConfig.LOCAL_CLIENT)),
				manager.getClients().stream().map(c -> c.getClientId()))
				.distinct()
				.sorted((String o1, String o2) -> {
					if (o1.equals(ToolsConfig.LOCAL_CLIENT)) {
						return -1;
					}
					if (o2.equals(ToolsConfig.LOCAL_CLIENT)) {
						return 1;
					}
					return o1.compareTo(o2);
				})
				.map(c -> RenderedConfigRemotePeerClient.of(manager, c, enabledClients.contains(c)))
				.collect(Collectors.toList());
		return new RenderedRemoteRepeatsClientsConfig(clients);
	}

	public List<RenderedConfigRemotePeerClient> getClients() {
		return clients;
	}
	public void setClients(List<RenderedConfigRemotePeerClient> clients) {
		this.clients = clients;
	}
}
