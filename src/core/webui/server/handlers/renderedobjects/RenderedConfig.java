package core.webui.server.handlers.renderedobjects;

import java.util.List;

import core.config.Config;
import core.recorder.Recorder;

public class RenderedConfig {
	private boolean recordMouseClickOnly;
	private boolean haltTaskByEscape;
	private boolean executeOnRelease;
	private boolean useClipboardToTypeString;
	private boolean runTaskWithServerConfig;
	private boolean useJavaAwtToGetMousePosition;
	private boolean useTrayIcon;
	private List<RenderedDebugLevel> debugLevels;

	public static RenderedConfig fromConfig(Config config, Recorder recorder) {
		RenderedConfig output = new RenderedConfig();
		output.haltTaskByEscape = config.isEnabledHaltingKeyPressed();
		output.executeOnRelease = config.isExecuteOnKeyReleased();
		output.useClipboardToTypeString = config.isUseClipboardToTypeString();
		output.runTaskWithServerConfig = config.isRunTaskWithServerConfig();
		output.recordMouseClickOnly = recorder.getRecordMode() == Recorder.MODE_MOUSE_CLICK_ONLY;
		output.useJavaAwtToGetMousePosition = config.isUseJavaAwtToGetMousePosition();
		output.useTrayIcon = config.isUseTrayIcon();
		output.debugLevels = RenderedDebugLevel.of(config.getNativeHookDebugLevel());
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

	public boolean isUseClipboardToTypeString() {
		return useClipboardToTypeString;
	}

	public void setUseClipboardToTypeString(boolean useClipboardToTypeString) {
		this.useClipboardToTypeString = useClipboardToTypeString;
	}

	public boolean isRunTaskWithServerConfig() {
		return runTaskWithServerConfig;
	}

	public void setRunTaskWithServerConfig(boolean runTaskWithServerConfig) {
		this.runTaskWithServerConfig = runTaskWithServerConfig;
	}

	public boolean isUseJavaAwtToGetMousePosition() {
		return useJavaAwtToGetMousePosition;
	}

	public void setUseJavaAwtToGetMousePosition(boolean useJavaAwtToGetMousePosition) {
		this.useJavaAwtToGetMousePosition = useJavaAwtToGetMousePosition;
	}

	public boolean isUseTrayIcon() {
		return useTrayIcon;
	}

	public void setUseTrayIcon(boolean useTrayIcon) {
		this.useTrayIcon = useTrayIcon;
	}

	public List<RenderedDebugLevel> getDebugLevels() {
		return debugLevels;
	}

	public void setDebugLevels(List<RenderedDebugLevel> debugLevels) {
		this.debugLevels = debugLevels;
	}
}
