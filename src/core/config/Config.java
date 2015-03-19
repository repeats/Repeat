package core.config;

import java.io.File;

import javax.swing.JOptionPane;

import utilities.FileUtility;
import utilities.JSONUtility;
import argo.jdom.JsonRootNode;

import com.sun.glass.events.KeyEvent;

public class Config {

	private static final String CONFIG_FILE_NAME = "config.json";

	public static int RECORD = KeyEvent.VK_F9;
	public static int REPLAY = KeyEvent.VK_F11;
	public static int COMPILED_REPLAY = KeyEvent.VK_F12;

	public static void loadConfig() {
		File config = new File(CONFIG_FILE_NAME);
		if (FileUtility.fileExists(config)) {
			JsonRootNode root = JSONUtility.readJSON(config);

			if (root == null) {
				JOptionPane.showMessageDialog(null, "Config file is not in json format");
			}

			try {
				String version = root.getStringValue("version");
				RECORD = Integer.parseInt(root.getNode("global_hotkey").getNumberValue("record"));
				REPLAY = Integer.parseInt(root.getNode("global_hotkey").getNumberValue("replay"));
				COMPILED_REPLAY = Integer.parseInt(root.getNode("global_hotkey").getNumberValue("replay_compiled"));
			} catch (Exception e) {
				//Ignore

			}
		}
	}
}
