package core.webui.server.handlers.renderedobjects;

import core.ipc.repeatClient.repeatPeerClient.RepeatsPeerServiceClient;
import core.ipc.repeatClient.repeatPeerClient.RepeatsPeerServiceClientManager;
import core.userDefinedTask.internals.ToolsConfig;

public class RenderedConfigRemotePeerClient {
	private String id;
	private String host;
	private String port;
	private String enabled;
	private String defunc;

	private RenderedConfigRemotePeerClient() {}

	public static RenderedConfigRemotePeerClient of(RepeatsPeerServiceClientManager manager, String clientId, boolean enabled) {
		RenderedConfigRemotePeerClient output = new RenderedConfigRemotePeerClient();
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