package core.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.ipc.IPCServiceManager;
import core.keyChain.GlobalEventsManager;
import core.keyChain.KeyChain;
import core.languageHandler.Language;
import core.userDefinedTask.TaskGroup;
import core.userDefinedTask.UserDefinedAction;
import utilities.JSONUtility;
import utilities.StringUtilities;

public class Parser2_2 extends ConfigParser {

	private static final Logger LOGGER = Logger.getLogger(Parser2_2.class.getName());

	@Override
	protected String getVersion() {
		return "2.2";
	}

	@Override
	protected String getPreviousVersion() {
		return "2.1";
	}

	@Override
	protected JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion) {
		try {
			// Add classpath to java compiler as compiler specific args
			List<JsonNode> compilers = previousVersion.getArrayNode("compilers");

			List<JsonNode> replacement = new ArrayList<>();
			for (JsonNode compiler : compilers) {
				if (compiler.getStringValue("name").equals(Language.JAVA.toString())) {
					replacement.add(JSONUtility.replaceChild(compiler, "compiler_specific_args",
							JsonNodeFactories.object(JsonNodeFactories.field("classpath", JsonNodeFactories.array()))));
				} else {
					replacement.add(compiler);
				}
			}

			return JSONUtility.replaceChild(previousVersion, "compilers", JsonNodeFactories.array(replacement)).getRootNode();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to convert json from previous version " + getPreviousVersion(), e);
			return null;
		}
	}

	@Override
	protected boolean internalExtractData(Config config, JsonRootNode root) {
		try {
			JsonNode globalSettings = root.getNode("global_settings");
			config.setUseTrayIcon(globalSettings.getBooleanValue("tray_icon_enabled"));
			config.setEnabledHaltingKeyPressed(globalSettings.getBooleanValue("enabled_halt_by_key"));
			config.setExecuteOnKeyReleased(globalSettings.getBooleanValue("execute_on_key_released"));
			config.setNativeHookDebugLevel(Level.parse(globalSettings.getNode("debug").getStringValue("level")));

			JsonNode globalHotkey = globalSettings.getNode("global_hotkey");

			String mouseGestureActivation = globalHotkey.getNumberValue("mouse_gesture_activation");
			config.setMouseGestureActivationKey(Integer.parseInt(mouseGestureActivation));
			config.setRECORD(KeyChain.parseJSON(globalHotkey.getArrayNode("record")));
			config.setREPLAY(KeyChain.parseJSON(globalHotkey.getArrayNode("replay")));
			config.setCOMPILED_REPLAY(KeyChain.parseJSON(globalHotkey.getArrayNode("replay_compiled")));

			List<JsonNode> ipcSettings = root.getArrayNode("ipc_settings");
			if (!IPCServiceManager.parseJSON(ipcSettings)) {
				LOGGER.log(Level.WARNING, "IPC Service Manager failed to parse JSON metadata");
			}

			if (!config.getCompilerFactory().parseJSON(root.getArrayNode("compilers"))) {
				LOGGER.log(Level.WARNING, "Dynamic Compiler Manager failed to parse JSON metadata");
			}

			List<TaskGroup> taskGroups = config.getBackEnd().getTaskGroups();
			taskGroups.clear();
			for (JsonNode taskGroupNode : root.getArrayNode("task_groups")) {
				TaskGroup taskGroup = TaskGroup.parseJSON(config.getCompilerFactory(), taskGroupNode);
				if (taskGroup != null) {
					taskGroups.add(taskGroup);
				}
			}

			if (taskGroups.isEmpty()) {
				taskGroups.add(new TaskGroup("default"));
			}
			config.getBackEnd().setCurrentTaskGroup(taskGroups.get(0));
			return true;
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to parse json", e);
			return false;
		}
	}

	@Override
	protected boolean internalImportData(Config config, JsonRootNode root) {
		boolean result = true;

		GlobalEventsManager keysManager = config.getBackEnd().getKeysManager();
		List<TaskGroup> taskGroups = config.getBackEnd().getTaskGroups();
		for (JsonNode taskGroupNode : root.getArrayNode("task_groups")) {
			TaskGroup taskGroup = TaskGroup.parseJSON(config.getCompilerFactory(), taskGroupNode);
			result &= taskGroup != null;
			if (taskGroup != null) {
				taskGroups.add(taskGroup);
				for (UserDefinedAction action : taskGroup.getTasks()) {
					Set<UserDefinedAction> collisions = keysManager.isActivationRegistered(action.getActivation());
					if (collisions.isEmpty()) {
						keysManager.registerTask(action);
					} else {
						result = false;
						String collisionNames = StringUtilities.join(collisions.stream().map(t -> t.getName()).collect(Collectors.toList()), ", ");
						LOGGER.log(Level.WARNING, "Cannot register action " + action.getName() + ". There are collisions with " + collisionNames + " in hotkeys!");
					}
				}
			}
		}
		return result;
	}
}