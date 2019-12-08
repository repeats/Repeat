package core.userDefinedTask;

import java.io.File;

import core.ipc.IIPCService;
import core.ipc.IPCServiceManager;
import core.ipc.IPCServiceName;
import core.userDefinedTask.internals.DefaultTools;
import core.userDefinedTask.internals.ITools;
import core.userDefinedTask.internals.LocalTools;

public class Tools {

	public static ITools local() {
		return LocalTools.of();
	}

	/**
	 * Get plain text (if possible) from system clipboard
	 * @return the plain text in the clipboard, or empty string if encounter an error
	 */
	public static String getClipboard() {
		return DefaultTools.get().getClipboard();
	}

	/**
	 * Set a text value into the system clipboard
	 * @param data string to copy to the system clipboard
	 * @return if action succeeds
	 */
	public static boolean setClipboard(String data) {
		return DefaultTools.get().setClipboard(data);
	}

	/**
	 * Execute a command in the environment
	 * @param command command to execute
	 * @return stdout of the command
	 */
	public static String execute(String command) {
		return DefaultTools.get().execute(command);
	}

	/**
	 * Execute a command in a specific directory
	 * @param command command to execute
	 * @param cwd directory where the command should be executed in
	 * @return stdout of the command
	 */
	public static String execute(String command, String cwd) {
		return execute(command, new File(cwd));
	}

	/**
	 * Execute a command in a specific directory
	 * @param command command to execute
	 * @param cwd directory where the command should be executed in
	 * @return stdout of the command
	 */
	public static String execute(String command, File cwd) {
		return DefaultTools.get().execute(command, cwd);
	}

	/**
	 * Contains utilities to access the system internals.
	 */
	public static class System {

		private System() {}

		/**
		 * Retrieves the UI server service.
		 */
		public static IIPCService getUiService() {
			return IPCServiceManager.getIPCService(IPCServiceName.WEB_UI_SERVER);
		}

		/**
		 * Retrieves the CLI server service.
		 */
		public static IIPCService getCliService() {
			return IPCServiceManager.getIPCService(IPCServiceName.CLI_SERVER);
		}
	}

	private Tools() {}
}
