package core.webui.server.handlers.renderedobjects;

import core.ipc.repeatClient.repeatPeerClient.RepeatsPeerServiceClient;

public class RenderedRepeatsRemoteClient {
	private String id;
	private String host;
	private String port;
	private String running;
	private String launchAtStartup;

	public static RenderedRepeatsRemoteClient fromRepeatsPeerServiceClient(RepeatsPeerServiceClient client) {
		RenderedRepeatsRemoteClient output = new RenderedRepeatsRemoteClient();
		output.id = client.getClientId();
		output.host = client.getHost();
		output.port = client.getPort() + "";
		output.running = client.isRunning() + "";
		output.launchAtStartup = client.isLaunchAtStartup() + "";
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
	public String getRunning() {
		return running;
	}
	public void setRunning(String running) {
		this.running = running;
	}
	public String getLaunchAtStartup() {
		return launchAtStartup;
	}
	public void setLaunchAtStartup(String launchAtStartup) {
		this.launchAtStartup = launchAtStartup;
	}
}
