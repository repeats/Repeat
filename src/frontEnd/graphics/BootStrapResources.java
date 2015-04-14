package frontEnd.graphics;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class BootStrapResources {

	private static final Logger LOGGER = Logger.getLogger(BootStrapResources.class.getName());
	public static final ImageIcon UP, DOWN, DELETE, ADD, EDIT, MOVE;
	public static final ImageIcon RECORD, STOP, PLAY, SELECT;

	static {
		ADD = getImage("/toolbarButtonGraphics/general/Add24.gif");
		EDIT = getImage("/toolbarButtonGraphics/general/Edit24.gif");
		DELETE = getImage("/toolbarButtonGraphics/general/Delete24.gif");

		MOVE = getImage("/toolbarButtonGraphics/general/Redo24.gif");

		UP = getImage("/toolbarButtonGraphics/navigation/Up24.gif");
		DOWN = getImage("/toolbarButtonGraphics/navigation/Down24.gif");

		RECORD = getImage("/toolbarButtonGraphics/general/Stop16.gif");
		STOP = getImage("/toolbarButtonGraphics/media/Stop16.gif");

		PLAY = getImage("/toolbarButtonGraphics/media/Play16.gif");

		SELECT = getImage("/toolbarButtonGraphics/general/Preferences24.gif");
	}

	private static ImageIcon getImage(String resource) {
		try {
			return new ImageIcon(ImageIO.read(BootStrapResources.class.getResourceAsStream(resource)));
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Cannot load resource " + resource, e);
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
