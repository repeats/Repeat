package core.config;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import utilities.FileUtility;
import utilities.JSONUtility;
import argo.jdom.JsonRootNode;

import com.sun.glass.events.KeyEvent;

public class Config {

	private static final String CONFIG_FILE_NAME = "config.json";

	public static int HALT_TASK = KeyEvent.VK_ESCAPE;
	public static int RECORD = KeyEvent.VK_F9;
	public static int REPLAY = KeyEvent.VK_F11;
	public static int COMPILED_REPLAY = KeyEvent.VK_F12;

	public static void loadConfig() {
		Map<String, ConfigParser> parsers = new HashMap<>();
		List<ConfigParser> knownParsers = Arrays.asList(new ConfigParser[]{
			new Parser1_0()
		});

		for (ConfigParser parser : knownParsers) {
			parsers.put(parser.getVersion(), parser);
		}


		File config = new File(CONFIG_FILE_NAME);
		if (FileUtility.fileExists(config)) {
			JsonRootNode root = JSONUtility.readJSON(config);

			if (root == null) {
				JOptionPane.showMessageDialog(null, "Config file is not in json format");
				return;
			} else if (!root.isStringValue("version")) {
				JOptionPane.showMessageDialog(null, "Config file is in unknown version");
				return;
			}

			String version = root.getStringValue("version");
			ConfigParser parser = parsers.get(version);
			if (parser == null) {
				JOptionPane.showMessageDialog(null, "Config file is in unknown version " + version);
			} else {
				parser.extractData(root);
			}
		}
	}
}
