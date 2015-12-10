package staticResources;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import utilities.FileUtility;
import core.config.Config;
import core.languageHandler.Language;

public class BootStrapResources {

	private static final Logger LOGGER = Logger.getLogger(BootStrapResources.class.getName());

	private static final Map<String, String> LANGUAGE_API;
	private static final Map<String, String> NATIVE_LANGUAGE_TEMPLATES;

	public static final Image TRAY_IMAGE;
	public static final ImageIcon UP, DOWN, DELETE, ADD, EDIT, MOVE, REFRESH;
	public static final ImageIcon RECORD, STOP, PLAY, SELECT;

	static {
		TRAY_IMAGE = getImage("/staticResources/Repeat.jpg");

		ADD = getIcon("/toolbarButtonGraphics/general/Add24.gif");
		EDIT = getIcon("/toolbarButtonGraphics/general/Edit24.gif");
		DELETE = getIcon("/toolbarButtonGraphics/general/Delete24.gif");

		MOVE = getIcon("/toolbarButtonGraphics/general/Redo24.gif");
		REFRESH = getIcon("/toolbarButtonGraphics/general/Refresh24.gif");

		UP = getIcon("/toolbarButtonGraphics/navigation/Up24.gif");
		DOWN = getIcon("/toolbarButtonGraphics/navigation/Down24.gif");

		RECORD = getIcon("/toolbarButtonGraphics/general/Stop16.gif");
		STOP = getIcon("/toolbarButtonGraphics/media/Stop16.gif");

		PLAY = getIcon("/toolbarButtonGraphics/media/Play16.gif");

		SELECT = getIcon("/toolbarButtonGraphics/general/Preferences24.gif");

		/*********************************************************************************/
		LANGUAGE_API = new HashMap<>();
		LANGUAGE_API.put(Language.JAVA.toString(), getFile("/core/languageHandler/API/JavaAPI.txt"));
		LANGUAGE_API.put(Language.PYTHON.toString(), getFile("/core/languageHandler/API/PythonAPI.txt"));

		NATIVE_LANGUAGE_TEMPLATES = new HashMap<>();
		NATIVE_LANGUAGE_TEMPLATES.put(Language.PYTHON.toString(), getFile("/python/template_repeat.py"));
	}

	public static void extractResources() throws IOException {
		PythonResources.extractResources();
	}

	private static ImageIcon getIcon(String resource) {
		return new ImageIcon(getImage(resource));
	}

	private static Image getImage(String resource) {
		try {
			return ImageIO.read(BootStrapResources.class.getResourceAsStream(resource));
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Cannot load image " + resource, e);
			return null;
		}
	}

	private static String getFile(String path) {
		return FileUtility.readFromStream(BootStrapResources.class.getResourceAsStream(path)).toString();
	}

	public static InputStream test() {
		return BootStrapResources.class.getResourceAsStream("/python");
	}

	public static String getAbout() {
		return "Repeat " + Config.RELEASE_VERSION + "\n"
				+ "A tool to repeat yourself with some intelligence.\n"
				+ "Created by Hoai Phuoc Truong. Contact me at hptruong93@gmail.com.";
	}

	public static String getAPI(String language) {
		if (LANGUAGE_API.containsKey(language)) {
			return LANGUAGE_API.get(language);
		} else {
			return "";
		}
	}

	public static String getNativeLanguageTemplate(String language) {
		if (NATIVE_LANGUAGE_TEMPLATES.containsKey(language)) {
			return NATIVE_LANGUAGE_TEMPLATES.get(language);
		} else {
			return "";
		}
	}

	private BootStrapResources() {}
}
