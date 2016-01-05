package utilities;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GeneralUtility {

	private static final Logger LOGGER = Logger.getLogger(GeneralUtility.class.getName());

	/**
	 * Copy a string to clipboard
	 * @param s string to be copied to clipboard
	 */
	public static void copyToClipboard(String s) {
		StringSelection stringSelection = new StringSelection(s);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
	}

	/**
	 * Retrieve text from clipboard
	 * @return text from clipboard, or empty string if error occurs.
	 */
	public static String copyFromClipboard() {
		try {
			String data = (String) Toolkit.getDefaultToolkit()
			        .getSystemClipboard().getData(DataFlavor.stringFlavor);
			return data;
		} catch (HeadlessException | UnsupportedFlavorException | IOException e) {
			LOGGER.log(Level.WARNING, "Error retrieving text from clipboard.", e);
			return "";
		}
	}

	private GeneralUtility() {}
}
