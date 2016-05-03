package core.config;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import utilities.Function;
import utilities.JSONUtility;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.ipc.IPCServiceManager;
import core.keyChain.KeyChain;
import core.languageHandler.Language;
import core.userDefinedTask.TaskGroup;

public class Parser1_8 extends ConfigParser {

	private static final Logger LOGGER = Logger.getLogger(Parser1_8.class.getName());

	@Override
	protected String getVersion() {
		return "1.8";
	}

	@Override
	protected String getPreviousVersion() {
		return "1.7";
	}

	@Override
	protected JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion) {
		try {
			JsonNode globalHotkeys = previousVersion.getNode("global_hotkey");
			JsonNode result = JSONUtility.removeChild(previousVersion, "global_hotkey");

			JsonNode globalSettings = JSONUtility.addChild(result.getNode("global_settings"), "global_hotkey", globalHotkeys);
			result = JSONUtility.replaceChild(result, "global_settings", globalSettings);
			result = JSONUtility.addChild(result, "ipc_settings", JsonNodeFactories.array(
					new Function<Language, JsonNode>(){
						@Override
						public JsonNode apply(Language l) {
							return JsonNodeFactories.object(
										JsonNodeFactories.field("name", JsonNodeFactories.string(l.toString())),
										JsonNodeFactories.field("launch_at_startup", JsonNodeFactories.booleanNode(true))
										);
						}
					}.map(Language.ALL_LANGUAGES)
				));

			return result.getRootNode();
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