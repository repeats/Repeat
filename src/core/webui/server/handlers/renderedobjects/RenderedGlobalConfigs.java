package core.webui.server.handlers.renderedobjects;

public class RenderedGlobalConfigs {
	private RenderedToolsConfig toolsConfigs;

	private RenderedGlobalConfigs() {}

	public static RenderedGlobalConfigs of(RenderedToolsConfig toolsConfigs) {
		RenderedGlobalConfigs output = new RenderedGlobalConfigs();
		output.toolsConfigs = toolsConfigs;
		return output;
	}

	public RenderedToolsConfig getToolsConfigs() {
		return toolsConfigs;
	}
	public void setToolsConfigs(RenderedToolsConfig toolsConfigs) {
		this.toolsConfigs = toolsConfigs;
	}
}
