package core.webui.server.handlers.renderedobjects;

import core.userDefinedTask.internals.RunActionConfig;

public class RenderedRunTaskConfig {
	private String repeatCount;
	private String delayMsBetweenRepeats;

	public static RenderedRunTaskConfig fromRunTaskConfig(RunActionConfig config) {
		RenderedRunTaskConfig result = new RenderedRunTaskConfig();
		result.repeatCount = config.getRepeatCount() + "";
		result.delayMsBetweenRepeats = config.getDelayMsBetweenRepeats() + "";
		return result;
	}

	public String getRepeatCount() {
		return repeatCount;
	}
	public String getDelayMsBetweenRepeats() {
		return delayMsBetweenRepeats;
	}
}
