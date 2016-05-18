package core.userDefinedTask;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import utilities.SubprocessUttility;

public class Tools {

	private static final Logger LOGGER = Logger.getLogger(Tools.class.getName());

	public static String getClipboard() {
		try {
			String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
			return data;
		} catch (HeadlessException | UnsupportedFlavorException | IOException e) {
			LOGGER.log(Level.WARNING, "Unable to retrieve text from clipboard", e);
			return "";
		}
	}

	public static String execute(String command) {
		return SubprocessUttility.execute(command);
	}

	public static String execute(String command, File cwd) {
		return SubprocessUttility.execute(command, cwd);
	}

	private Tools() {}
}
