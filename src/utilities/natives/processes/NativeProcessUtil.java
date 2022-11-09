package utilities.natives.processes;

import utilities.OSIdentifier;

/**
 * Provides utility to interact with processes via native APIs.
 */
public class NativeProcessUtil {

	public static NativeWindowInfo getActiveWindowInfo() {
		if (OSIdentifier.IS_WINDOWS) {
			return WindowsNativeProcessUtil.getActiveWindowInfo();
		}
		if (OSIdentifier.IS_LINUX) {
			throw new IllegalStateException("OS is not supported.");
		}
		if (OSIdentifier.IS_OSX) {
			return OSXNativeProcessUtil.getActiveWindowInfo();
		}
		throw new IllegalStateException("OS is not supported.");
	}

	/**
	 * Contains information for native window.
	 */
	public static class NativeWindowInfo {
		private String title;
		// Name of the active process.
		// For OSX this is the active application.
		private String processName;

		static NativeWindowInfo of(String title, String processName) {
			NativeWindowInfo result = new NativeWindowInfo();
			result.title = title;
			result.processName = processName;
			return result;
		}

		public String getTitle() {
			return title;
		}

		public String getProcessName() {
			return processName;
		}
	}

	private NativeProcessUtil() {}
}
