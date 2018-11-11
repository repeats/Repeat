package core.userDefinedTask;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import core.ipc.IIPCService;
import core.ipc.IPCServiceManager;
import core.ipc.IPCServiceName;
import utilities.SubprocessUttility;

public class Tools {

	private static final Logger LOGGER = Logger.getLogger(Tools.class.getName());

	/**
	 * Get plain text (if possible) from system clipboard
	 * @return the plain text in the clipboard, or empty string if encounter an error
	 */
	public static String getClipboard() {
		try {
			String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
			return data;
		} catch (HeadlessException | UnsupportedFlavorException | IOException e) {
			LOGGER.log(Level.WARNING, "Unable to retrieve text from clipboard", e);
			return "";
		}
	}

	/**
	 * Set a text value into the system clipboard
	 * @param data string to copy to the system clipboard
	 * @return if action succeeds
	 */
	public static boolean setClipboard(String data) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection selection = new StringSelection(data);
		clipboard.setContents(selection, null);

		return true;
	}

	/**
	 * Execute a command in the environment
	 * @param command command to execute
	 * @return stdout of the command
	 */
	public static String execute(String command) {
		return SubprocessUttility.execute(command);
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
		return SubprocessUttility.execute(command, cwd);
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
