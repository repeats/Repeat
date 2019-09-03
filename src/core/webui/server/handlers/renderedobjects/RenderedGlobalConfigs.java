package core.webui.server.handlers.renderedobjects;

public class RenderedGlobalConfigs {
	private RenderedRemoteRepeatsClientsConfig toolsConfigs;
	private RenderedRemoteRepeatsClientsConfig coreConfigs;
	private RenderedRemoteRepeatsClientsConfig remoteRepeatsCompilerConfigs;

	private RenderedGlobalConfigs() {}

	public static RenderedGlobalConfigs of(RenderedRemoteRepeatsClientsConfig toolsConfigs, RenderedRemoteRepeatsClientsConfig coreConfigs, RenderedRemoteRepeatsClientsConfig remoteRepeatsCompilerConfigs) {
		RenderedGlobalConfigs output = new RenderedGlobalConfigs();
		output.toolsConfigs = toolsConfigs;
		output.coreConfigs = coreConfigs;
		output.remoteRepeatsCompilerConfigs = remoteRepeatsCompilerConfigs;
		return output;
	}

	public RenderedRemoteRepeatsClientsConfig getToolsConfigs() {
		return toolsConfigs;
	}
	public void setToolsConfigs(RenderedRemoteRepeatsClientsConfig toolsConfigs) {
		this.toolsConfigs = toolsConfigs;
	}
	public RenderedRemoteRepeatsClientsConfig getCoreConfigs() {
		return coreConfigs;
	}
	public void setCoreConfigs(RenderedRemoteRepeatsClientsConfig coreConfigs) {
		this.coreConfigs = coreConfigs;
	}
	public RenderedRemoteRepeatsClientsConfig getRemoteRepeatsCompilerConfigs() {
		return remoteRepeatsCompilerConfigs;
	}
	public void setRemoteRepeatsCompilerConfigs(RenderedRemoteRepeatsClientsConfig remoteRepeatsCompilerConfigs) {
		this.remoteRepeatsCompilerConfigs = remoteRepeatsCompilerConfigs;
	}
}
