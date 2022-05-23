package core.config;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.cli.server.CliServer;
import core.controller.CoreConfig;
import core.ipc.IPCServiceManager;
import core.ipc.IPCServiceName;
import core.ipc.repeatClient.repeatPeerClient.RepeatsPeerServiceClientManager;
import core.keyChain.KeyChain;
import core.userDefinedTask.TaskGroup;
import core.userDefinedTask.internals.ToolsConfig;
import utilities.json.JSONUtility;

public class Parser2_14 extends ConfigParser {

	private static final Logger LOGGER = Logger.getLogger(Parser2_14.class.getName());

	@Override
	protected String getVersion() {
		return "2.14";
	}

	@Override
	protected String getPreviousVersion() {
		return "2.13";
	}

	@Override
	protected JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion) {
		JsonNode taskGroups = previousVersion.getNode("task_groups");
		return JSONUtility.replaceChild(previousVersion, "task_groups", convertTaskGroups(taskGroups)).getRootNode();
	}

	private JsonNode convertTaskGroups(JsonNode node) {
		List<JsonNode> groups = node.getArrayNode();
		List<JsonNode> convertedGroups = new ArrayList<>();
		for (JsonNode group : groups) {
			List<JsonNode> convertedTasks = group.getArrayNode("tasks").stream().map(task -> convertTask(task)).collect(Collectors.toList());
			convertedGroups.add(JSONUtility.replaceChild(group, "tasks", JsonNodeFactories.array(convertedTasks)));
		}

		return JsonNodeFactories.array(convertedGroups);
	}

	private JsonNode convertTask(JsonNode node) {
		String currentPath = node.getStringValue("source_path");
		JsonNode currentSource = JsonNodeFactories.object(
				JsonNodeFactories.field("path", JsonNodeFactories.string(currentPath)),
				JsonNodeFactories.field("created_time", JsonNodeFactories.number(System.currentTimeMillis())));

		return JSONUtility.addChild(node, "source_history", JsonNodeFactories.object(
				JsonNodeFactories.field("entries", JsonNodeFactories.array(currentSource))));
	}

	@Override
	protected boolean internalExtractData(Config config, JsonRootNode root) {
		try {
			JsonNode globalSettings = root.getNode("global_settings");
			config.setUseTrayIcon(globalSettings.getBooleanValue("tray_icon_enabled"));
			config.setEnabledHaltingKeyPressed(globalSettings.getBooleanValue("enabled_halt_by_key"));
			config.setExecuteOnKeyReleased(globalSettings.getBooleanValue("execute_on_key_released"));
			config.setUseClipboardToTypeString(globalSettings.getBooleanValue("use_clipboard_to_type_string"));
			config.setRunTaskWithServerConfig(globalSettings.getBooleanValue("run_task_with_server_config"));
			config.setUseJavaAwtToGetMousePosition(globalSettings.getBooleanValue("use_java_awt_for_mouse_position"));

			config.setNativeHookDebugLevel(Level.parse(globalSettings.getNode("debug").getStringValue("level")));

			JsonNode globalHotkey = globalSettings.getNode("global_hotkey");

			String mouseGestureActivation = globalHotkey.getNumberValue("mouse_gesture_activation");
			config.setMouseGestureActivationKey(Integer.parseInt(mouseGestureActivation));
			config.setRECORD(KeyChain.parseJSON(globalHotkey.getArrayNode("record")));
			config.setREPLAY(KeyChain.parseJSON(globalHotkey.getArrayNode("replay")));
			config.setCOMPILED_REPLAY(KeyChain.parseJSON(globalHotkey.getArrayNode("replay_compiled")));

			JsonNode toolsConfigNode = globalSettings.getNode("tools_config");
			ToolsConfig toolsConfig = ToolsConfig.parseJSON(toolsConfigNode);
			config.setToolsConfig(toolsConfig);

			JsonNode coreConfigNode = globalSettings.getNode("core_config");
			CoreConfig coreConfig = CoreConfig.parseJSON(coreConfigNode);
			config.setCoreConfig(coreConfig);

			JsonNode peerClients = root.getNode("remote_repeats_clients");
			RepeatsPeerServiceClientManager repeatsPeerServiceClientManager = RepeatsPeerServiceClientManager.parseJSON(peerClients);
			config.getBackEnd().getPeerServiceClientManager().updateClients(repeatsPeerServiceClientManager.getClients());

			List<JsonNode> ipcSettings = root.getArrayNode("ipc_settings");
			if (!IPCServiceManager.parseJSON(ipcSettings)) {
				LOGGER.log(Level.WARNING, "IPC Service Manager failed to parse JSON metadata");
			}

			if (!config.getCompilerFactory().parseJSON(root.getNode("compilers"))) {
				LOGGER.log(Level.WARNING, "Dynamic Compiler Manager failed to parse JSON metadata");
			}

			config.getBackEnd().clearTaskGroup();
			for (JsonNode taskGroupNode : root.getArrayNode("task_groups")) {
				TaskGroup taskGroup = TaskGroup.parseJSON(config.getCompilerFactory(), taskGroupNode);
				if (taskGroup != null) {
					config.getBackEnd().addTaskGroup(taskGroup);
				}
			}

			if (config.getBackEnd().getTaskGroups().isEmpty()) {
				config.getBackEnd().addTaskGroup(new TaskGroup("default"));
			}
			config.getBackEnd().setCurrentTaskGroup(config.getBackEnd().getTaskGroups().get(0));
			return true;
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to parse json", e);
			return false;
		}
	}

	@Override
	protected boolean internalImportData(Config config, JsonRootNode root) {
		boolean result = true;

		for (JsonNode taskGroupNode : root.getArrayNode("task_groups")) {
			TaskGroup taskGroup = TaskGroup.parseJSON(config.getCompilerFactory(), taskGroupNode);
			result &= taskGroup != null;
			if (taskGroup != null) {
				result &= config.getBackEnd().addPopulatedTaskGroup(taskGroup);
			}
		}
		return result;
	}

	@Override
	protected boolean internalExtractData(CliConfig config, JsonRootNode root) {
		try {
			List<JsonNode> ipcSettings = root.getArrayNode("ipc_settings");
			if (!IPCServiceManager.parseJSON(ipcSettings)) {
				LOGGER.log(Level.WARNING, "IPC Service Manager failed to parse JSON metadata");
			}

			CliServer cliServer = (CliServer) IPCServiceManager.getIPCService(IPCServiceName.CLI_SERVER);
			config.setServerPort(cliServer.getPort());
			return true;
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to parse json", e);
			return false;
		}
	}
}