package core.webui.server.handlers.renderedobjects;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import core.ipc.repeatClient.repeatPeerClient.RepeatsPeerServiceClient;
import core.ipc.repeatClient.repeatPeerClient.RepeatsPeerServiceClientManager;
import core.userDefinedTask.internals.ToolsConfig;

public class RenderedToolsConfig {

	private List<RenderedToolsClient> clients;

	private RenderedToolsConfig(List<RenderedToolsClient> clients) {
		this.clients = clients;
	}

	public static RenderedToolsConfig of(RepeatsPeerServiceClientManager manager, ToolsConfig toolsConfig) {
		Set<String> enabledClients = toolsConfig.getClients().stream().collect(Collectors.toSet());

		List<RenderedToolsClient> clients = Stream.concat(
				Stream.concat(toolsConfig.getClients().stream(), Stream.of(ToolsConfig.LOCAL_CLIENT)),
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
				.map(c -> RenderedToolsClient.of(manager, c, enabledClients.contains(c)))
				.collect(Collectors.toList());
		return new RenderedToolsConfig(clients);
	}

	public List<RenderedToolsClient> getClients() {
		return clients;
	}
	public void setClients(List<RenderedToolsClient> clients) {
		this.clients = clients;
	}

	public static class RenderedToolsClient {
		private String id;
		private String host;
		private String port;
		private String enabled;
		private String defunc;

		private RenderedToolsClient() {}

		public static RenderedToolsClient of(RepeatsPeerServiceClientManager manager, String clientId, boolean enabled) {
			RenderedToolsClient output = new RenderedToolsClient();
			output.enabled = enabled + "";
			if (clientId.equals(ToolsConfig.LOCAL_CLIENT)) {
				output.id = "local";
				output.host = "localhost";
				output.port = "N/A";
				output.defunc = false + "";
				return output;
			}
			output.id = clientId;
			RepeatsPeerServiceClient client = manager.getClient(clientId);
			if (client == null) {
				output.host = "Unknown";
				output.port = "Unknown";
				output.defunc = true + "";
			} else {
				output.host = client.getHost();
				output.port = client.getPort() + "";
				output.defunc = !client.isRunning() + "";
			}

			return output;
		}

		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getHost() {
			return host;
		}
		public void setHost(String host) {
			this.host = host;
		}
		public String getPort() {
			return port;
		}
		public void setPort(String port) {
			this.port = port;
		}
		public String getEnabled() {
			return enabled;
		}
		public void setEnabled(String enabled) {
			this.enabled = enabled;
		}
		public String getDefunc() {
			return defunc;
		}
		public void setDefunc(String defunc) {
			this.defunc = defunc;
		}
	}
}
