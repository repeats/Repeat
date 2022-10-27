package utilities.natives.processes;

import utilities.OSIdentifier;

/**
 * Provides utility to interact with processes via native APIs.
 */
public class NativeProcessUtil {

	public static String getActiveWindowTitle() {
		if (OSIdentifier.IS_WINDOWS) {
			return WindowsNativeProcessUtil.getActiveWindowTitle();
		}
		throw new IllegalStateException("OS is not supported.");
	}

	public static String getActiveWindowProcessName() {
		if (OSIdentifier.IS_WINDOWS) {
			return WindowsNativeProcessUtil.getActiveWindowProcessName();
		}
		throw new IllegalStateException("OS is not supported.");
	}

	private NativeProcessUtil() {}
}
