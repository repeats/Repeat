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

import core.KeyChain;
import core.UserDefinedAction;
import core.languageHandler.compiler.DynamicCompilerFactory;
import frontEnd.BackEndHolder;

public class Config {

	private static final String CONFIG_FILE_NAME = "config.json";
	private static final String CURRENT_VERSION = "1.0";

	private DynamicCompilerFactory compilerFactory;
	private final BackEndHolder backEnd;

	public final int HALT_TASK = KeyEvent.VK_ESCAPE; //This should be hardcoded, and should not be changed
	private KeyChain RECORD;
	private KeyChain REPLAY;
	private KeyChain COMPILED_REPLAY;

	public Config(BackEndHolder backEnd) {
		this.backEnd = backEnd;

		RECORD = new KeyChain(KeyEvent.VK_F9);
		REPLAY = new KeyChain(KeyEvent.VK_F11);
		COMPILED_REPLAY = new KeyChain(KeyEvent.VK_F12);
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
						JsonNodeFactories.field("record", RECORD.jsonize()),
						JsonNodeFactories.field("replay", REPLAY.jsonize()),
						JsonNodeFactories.field("replay_compiled", COMPILED_REPLAY.jsonize())
				)));

		return FileUtility.writeToFile(JSONUtility.jsonToString(root), new File(CONFIG_FILE_NAME), false);
	}

	public KeyChain getRECORD() {
		return RECORD;
	}

	public void setRECORD(KeyChain RECORD) {
		if (RECORD != null) {
			this.RECORD = RECORD;
		}
	}

	public void setRECORD(int RECORD) {
		setRECORD(new KeyChain(Arrays.asList(RECORD)));
	}

	public KeyChain getREPLAY() {
		return REPLAY;
	}

	public void setREPLAY(KeyChain REPLAY) {
		if (REPLAY != null) {
			this.REPLAY = REPLAY;
		}
	}

	public void setREPLAY(int REPLAY) {
		setREPLAY(new KeyChain(Arrays.asList(REPLAY)));
	}

	public KeyChain getCOMPILED_REPLAY() {
		return COMPILED_REPLAY;
	}

	public void setCOMPILED_REPLAY(KeyChain COMPILED_REPLAY) {
		if (COMPILED_REPLAY != null) {
			this.COMPILED_REPLAY = COMPILED_REPLAY;
		}
	}

	public void setCOMPILED_REPLAY(int COMPILED_REPLAY) {
		setCOMPILED_REPLAY(new KeyChain(Arrays.asList(COMPILED_REPLAY)));
	}

	protected BackEndHolder getBackEnd() {
		return backEnd;
	}
}
