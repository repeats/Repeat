package frontEnd.graphics;

import java.awt.Image;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class BootStrapResources {

	private static final Logger LOGGER = Logger.getLogger(BootStrapResources.class.getName());
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
		return "Created by HP Truong hptruong93@gmail.com.";
	}

	public static String getAPI(String language) {
		if (language.equals("java")) {
			return "";
		} else if (language.equals("python")) {
			return "";
		} else {
			return "";
		}
	}

	private BootStrapResources() {}
}
