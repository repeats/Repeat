package core.config;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import utilities.JSONUtility;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.ipc.IPCServiceManager;
import core.keyChain.KeyChain;
import core.userDefinedTask.TaskGroup;

public class Parser1_9 extends ConfigParser {

	private static final Logger LOGGER = Logger.getLogger(Parser1_9.class.getName());

	@Override
	protected String getVersion() {
		return "1.9";
	}

	@Override
	protected String getPreviousVersion() {
		return "1.8";
	}

	@Override
	protected JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion) {
		try {
			JsonNode globalSettings = previousVersion.getNode("global_settings");
			globalSettings = JSONUtility.addChild(globalSettings,
													"execute_on_key_released",
													JsonNodeFactories.booleanNode(false));
			return JSONUtility.replaceChild(previousVersion, "global_settings", globalSettings).getRootNode();
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
	protected boolean importData(Config config, JsonRootNode root) {
		boolean result = true;

		List<TaskGroup> taskGroups = config.getBackEnd().getTaskGroups();
		for (JsonNode taskGroupNode : root.getArrayNode("task_groups")) {
			TaskGroup taskGroup = TaskGroup.parseJSON(config.getCompilerFactory(), taskGroupNode);
			result &= taskGroup != null;
			if (taskGroup != null) {
				taskGroups.add(taskGroup);
			}
		}
		return result;
	}
}