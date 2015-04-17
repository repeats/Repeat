package frontEnd.graphics;

import java.awt.Image;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import utilities.FileUtility;
import core.config.Config;

public class BootStrapResources {

	private static final Logger LOGGER = Logger.getLogger(BootStrapResources.class.getName());

	private static final Map<String, String> LANGUAGE_API;

	public static final Image TRAY_IMAGE;
	public static final ImageIcon UP, DOWN, DELETE, ADD, EDIT, MOVE;
	public static final ImageIcon RECORD, STOP, PLAY, SELECT;

	static {
		TRAY_IMAGE = getImage("/frontEnd/graphics/Repeat.jpg");

		ADD = getIcon("/toolbarButtonGraphics/general/Add24.gif");
		EDIT = getIcon("/toolbarButtonGraphics/general/Edit24.gif");
		DELETE = getIcon("/toolbarButtonGraphics/general/Delete24.gif");

		MOVE = getIcon("/toolbarButtonGraphics/general/Redo24.gif");

		UP = getIcon("/toolbarButtonGraphics/navigation/Up24.gif");
		DOWN = getIcon("/toolbarButtonGraphics/navigation/Down24.gif");

		RECORD = getIcon("/toolbarButtonGraphics/general/Stop16.gif");
		STOP = getIcon("/toolbarButtonGraphics/media/Stop16.gif");

		PLAY = getIcon("/toolbarButtonGraphics/media/Play16.gif");

		SELECT = getIcon("/toolbarButtonGraphics/general/Preferences24.gif");

		/*********************************************************************************/
		LANGUAGE_API = new HashMap<>();
		LANGUAGE_API.put("java", FileUtility.readFromStream(BootStrapResources.class.getResourceAsStream("/core/languageHandler/API/JavaAPI.txt")).toString());
		LANGUAGE_API.put("python", FileUtility.readFromStream(BootStrapResources.class.getResourceAsStream("/core/languageHandler/API/PythonAPI.txt")).toString());
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

	private BootStrapResources() {}
}
