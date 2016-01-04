package core.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import utilities.FileUtility;
import utilities.JSONUtility;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;

import com.sun.glass.events.KeyEvent;

import core.keyChain.KeyChain;
import core.languageHandler.compiler.DynamicCompilerManager;
import core.userDefinedTask.TaskGroup;
import frontEnd.MainBackEndHolder;

public class Config {

	public static final String RELEASE_VERSION = "2.1";
	private static final String CONFIG_FILE_NAME = "config.json";
	private static final String CURRENT_CONFIG_VERSION = "1.7";

	private static final Level DEFAULT_NATIVE_HOOK_DEBUG_LEVEL = Level.WARNING;
	private static final boolean DEFAULT_TRAY_ICON_USE = true;

	private DynamicCompilerManager compilerFactory;
	private final MainBackEndHolder backEnd;

	public static final int HALT_TASK = KeyEvent.VK_ESCAPE; //This should be hardcoded, and must not be changed
	private KeyChain RECORD;
	private KeyChain REPLAY;
	private KeyChain COMPILED_REPLAY;

	private boolean useTrayIcon;
	private boolean enabledHaltingKeyPressed;
	private Level nativeHookDebugLevel;


	public Config(MainBackEndHolder backEnd) {
		this.backEnd = backEnd;
		useTrayIcon = DEFAULT_TRAY_ICON_USE;
		this.enabledHaltingKeyPressed = true;
		this.nativeHookDebugLevel = DEFAULT_NATIVE_HOOK_DEBUG_LEVEL;

		RECORD = new KeyChain(KeyEvent.VK_F9);
		REPLAY = new KeyChain(KeyEvent.VK_F11);
		COMPILED_REPLAY = new KeyChain(KeyEvent.VK_F12);
	}

	public DynamicCompilerManager getCompilerFactory() {
		return compilerFactory;
	}

	public void loadConfig(File file) {
		compilerFactory = new DynamicCompilerManager();

		List<ConfigParser> knownParsers = Arrays.asList(new ConfigParser[]{
			new Parser1_0(),
			new Parser1_1(),
			new Parser1_2(),
			new Parser1_3(),
			new Parser1_4(),
			new Parser1_5(),
			new Parser1_6(),
			new Parser1_7()
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
			boolean extractResult = false;
			for (ConfigParser parser : knownParsers) {
				if (parser.getVersion().equals(version)) {
					foundVersion = true;
					extractResult = parser.extractData(this, root);
					break;
				}
			}

			if (!foundVersion) {
				JOptionPane.showMessageDialog(null, "Config file is in unknown version " + version);
				defaultExtract();
			}

			if (!extractResult) {
				JOptionPane.showMessageDialog(null, "Cannot extract result with version " + version);
				defaultExtract();
			}
		} else {
			defaultExtract();
		}
	}

	private void defaultExtract() {
		List<TaskGroup> taskGroups = backEnd.getTaskGroups();
		taskGroups.add(new TaskGroup("default"));
		backEnd.setCurrentTaskGroup(taskGroups.get(0));
	}

	public boolean writeConfig() {
		List<JsonNode> taskNodes = new ArrayList<>();
		for (TaskGroup group : backEnd.getTaskGroups()) {
			taskNodes.add(group.jsonize());
		}

		JsonRootNode root = JsonNodeFactories.object(
				JsonNodeFactories.field("version", JsonNodeFactories.string(CURRENT_CONFIG_VERSION)),
				JsonNodeFactories.field("global_settings", JsonNodeFactories.object(
						JsonNodeFactories.field("debug", JsonNodeFactories.object(
								JsonNodeFactories.field("level", JsonNodeFactories.string(nativeHookDebugLevel.toString()))
								)),
						JsonNodeFactories.field("tray_icon_enabled", JsonNodeFactories.booleanNode(useTrayIcon)),
						JsonNodeFactories.field("enabled_halt_by_key", JsonNodeFactories.booleanNode(enabledHaltingKeyPressed))
						)),
				JsonNodeFactories.field("compilers", compilerFactory.jsonize()),
				JsonNodeFactories.field("task_groups", JsonNodeFactories.array(taskNodes)),
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

	public KeyChain getCOMPILED_REPLAY() {
		return COMPILED_REPLAY;
	}

	public void setCOMPILED_REPLAY(KeyChain COMPILED_REPLAY) {
		if (COMPILED_REPLAY != null) {
			this.COMPILED_REPLAY = COMPILED_REPLAY;
		}
	}

	public boolean isUseTrayIcon() {
		return useTrayIcon;
	}

	public void setUseTrayIcon(boolean useTrayIcon) {
		this.useTrayIcon = useTrayIcon;
	}

	public Level getNativeHookDebugLevel() {
		return nativeHookDebugLevel;
	}

	public void setNativeHookDebugLevel(Level nativeHookDebugLevel) {
		this.nativeHookDebugLevel = nativeHookDebugLevel;
	}

	protected MainBackEndHolder getBackEnd() {
		return backEnd;
	}

	public boolean isEnabledHaltingKeyPressed() {
		return enabledHaltingKeyPressed;
	}

	public void setEnabledHaltingKeyPressed(boolean enabledHaltingKeyPressed) {
		this.enabledHaltingKeyPressed = enabledHaltingKeyPressed;
	}
}
