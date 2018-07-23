package core.webui.server.handlers.renderedobjects;

import core.config.Config;
import core.recorder.Recorder;

public class RenderedConfig {
	private boolean recordMouseClickOnly;
	private boolean haltTaskByEscape;
	private boolean executeOnRelease;

	public static RenderedConfig fromConfig(Config config, Recorder recorder) {
		RenderedConfig output = new RenderedConfig();
		output.haltTaskByEscape = config.isEnabledHaltingKeyPressed();
		output.executeOnRelease = config.isExecuteOnKeyReleased();
		output.recordMouseClickOnly = recorder.getRecordMode() == Recorder.MODE_MOUSE_CLICK_ONLY;
		return output;
	}

	public boolean isRecordMouseClickOnly() {
		return recordMouseClickOnly;
	}

	public void setRecordMouseClickOnly(boolean recordMouseClickOnly) {
		this.recordMouseClickOnly = recordMouseClickOnly;
	}

	public boolean isHaltTaskByEscape() {
		return haltTaskByEscape;
	}

	public void setHaltTaskByEscape(boolean haltTaskByEscape) {
		this.haltTaskByEscape = haltTaskByEscape;
	}

	public boolean isExecuteOnRelease() {
		return executeOnRelease;
	}

	public void setExecuteOnRelease(boolean executeOnRelease) {
		this.executeOnRelease = executeOnRelease;
	}
}
