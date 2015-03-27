package core.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

import utilities.FileUtility;
import utilities.JSONUtility;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;

import com.sun.glass.events.KeyEvent;

import core.UserDefinedAction;
import core.languageHandler.compiler.DynamicCompilerFactory;
import frontEnd.BackEndHolder;

public class Config {

	private static final String CONFIG_FILE_NAME = "config.json";
	private static final String CURRENT_VERSION = "1.0";

	private DynamicCompilerFactory compilerFactory;
	private final BackEndHolder backEnd;

	public int HALT_TASK = KeyEvent.VK_ESCAPE;
	public int RECORD = KeyEvent.VK_F9;
	public int REPLAY = KeyEvent.VK_F11;
	public int COMPILED_REPLAY = KeyEvent.VK_F12;

	public Config(BackEndHolder backEnd) {
		this.backEnd = backEnd;
	}

	public DynamicCompilerFactory compilerFactory() {
		return compilerFactory;
	}

	public void loadConfig(File file) {
		compilerFactory = new DynamicCompilerFactory();

		List<ConfigParser> knownParsers = Arrays.asList(new ConfigParser[]{
			new Parser1_0()
		});

		File configFile = file == null ? new File(CONFIG_FILE_NAME) : file;
		if (FileUtility.fileExists(configFile)) {
			JsonRootNode root = JSONUtility.readJSON(configFile);

			if (root == null) {
				JOptionPane.showMessageDialog(null, "Config file is not in json format");
				return;
			} else if (!root.isStringValue("version")) {
				JOptionPane.showMessageDialog(null, "Config file is in unknown version");
				return;
			}

			String version = root.getStringValue("version");
			boolean foundVersion = false;
			for (ConfigParser parser : knownParsers) {
				if (parser.getVersion().equals(version)) {
					parser.extractData(this, root);
					foundVersion = true;
					break;
				}
			}

			if (!foundVersion) {
				JOptionPane.showMessageDialog(null, "Config file is in unknown version " + version);
			}
		}
	}

	public boolean writeConfig() {
		List<JsonNode> taskNodes = new ArrayList<>();
		for (UserDefinedAction action : backEnd.getCustomTasks()) {
			taskNodes.add(action.jsonize());
		}

		JsonRootNode root = JsonNodeFactories.object(
				JsonNodeFactories.field("version", JsonNodeFactories.string(CURRENT_VERSION)),
				JsonNodeFactories.field("compilers", compilerFactory.jsonize()),
				JsonNodeFactories.field("tasks", JsonNodeFactories.array(taskNodes)),
				JsonNodeFactories.field("global_hotkey", JsonNodeFactories.object(
						JsonNodeFactories.field("record", JsonNodeFactories.number(RECORD)),
						JsonNodeFactories.field("replay", JsonNodeFactories.number(REPLAY)),
						JsonNodeFactories.field("replay_compiled", JsonNodeFactories.number(COMPILED_REPLAY))))
				);

		return FileUtility.writeToFile(JSONUtility.jsonToString(root), new File(CONFIG_FILE_NAME), false);
	}

	protected BackEndHolder getBackEnd() {
		return backEnd;
	}
}
