package utilities.natives.processes;

import java.util.logging.Level;
import java.util.logging.Logger;

import utilities.SubprocessUttility;
import utilities.SubprocessUttility.ExecutionException;

/**
 * Provides interaction with OSX processes via AppleScript.
 */
final class OSXNativeProcessUtil {

	private static final Logger LOGGER = Logger.getLogger(OSXNativeProcessUtil.class.getName());

	static final String[] ACTIVE_WINDOW = new String[] {
			"osascript",
			"-e", "global frontApp, frontAppName, windowTitle",
			"-e", "set windowTitle to \"\"",
			"-e", "tell application \"System Events\"",
			"-e", "set frontApp to first application process whose frontmost is true",
			"-e", "set frontAppName to name of frontApp",
			"-e", "tell process frontAppName",
			"-e", "tell (1st window whose value of attribute \"AXMain\" is true)",
			"-e", "set windowTitle to value of attribute \"AXTitle\"",
			"-e", "end tell",
			"-e", "end tell",
			"-e", "log windowTitle",
			"-e", "log frontAppName",
			"-e", "end",
	};

	public static NativeProcessUtil.NativeWindowInfo getActiveWindowInfo() {
		String execResult = executeActiveWindowTitleCmd();
		if (execResult == null || execResult.isEmpty()) {
			return NativeProcessUtil.NativeWindowInfo.of("", "");
		}

		execResult = execResult.trim();
		String[] parts = execResult.split("\n");
		if (parts.length != 2) {
			LOGGER.warning("Error parsing exec result to get active window info: " + execResult);
			return NativeProcessUtil.NativeWindowInfo.of(execResult, "");
		}

		return NativeProcessUtil.NativeWindowInfo.of(parts[0], parts[1]);
	}

	private static String executeActiveWindowTitleCmd() {
		try {
			String[] outputs = SubprocessUttility.execute(ACTIVE_WINDOW, "");
			return outputs[1];
		} catch (ExecutionException e) {
			LOGGER.log(Level.WARNING, "Exception when fetching active window title.", e);
		}
		return "";
	}

	private OSXNativeProcessUtil() {}
}
