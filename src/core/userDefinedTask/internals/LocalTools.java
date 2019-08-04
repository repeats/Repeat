package core.userDefinedTask.internals;

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

import utilities.SubprocessUttility;
import utilities.SubprocessUttility.ExecutionException;

public class LocalTools implements ITools {

	private static final Logger LOGGER = Logger.getLogger(LocalTools.class.getName());

	private static final LocalTools INSTANCE = new LocalTools();

	private LocalTools() {}

	public static LocalTools of() {
		return INSTANCE;
	}

	@Override
	public String getClipboard() {
		try {
			String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
			return data;
		} catch (HeadlessException | UnsupportedFlavorException | IOException e) {
			LOGGER.log(Level.WARNING, "Unable to retrieve text from clipboard", e);
			return "";
		}
	}

	@Override
	public boolean setClipboard(String data) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection selection = new StringSelection(data);
		clipboard.setContents(selection, null);

		return true;
	}

	@Override
	public String execute(String command) {
		try {
			return SubprocessUttility.execute(command);
		} catch (ExecutionException e) {
			return "";
		}
	}

	@Override
	public String execute(String command, File cwd) {
		try {
			return SubprocessUttility.execute(command, cwd);
		} catch (ExecutionException e) {
			return "";
		}
	}
}
