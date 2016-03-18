package core.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import utilities.JSONUtility;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.keyChain.KeyChain;
import core.languageHandler.compiler.AbstractNativeCompiler;
import core.userDefinedTask.TaskGroup;
import core.userDefinedTask.UsageStatistics;

public class Parser1_7 extends ConfigParser {

	private static final Logger LOGGER = Logger.getLogger(Parser1_7.class.getName());

	@Override
	protected String getVersion() {
		return "1.7";
	}

	@Override
	protected String getPreviousVersion() {
		return "1.6";
	}

	private JsonNode internalTaskConversion(JsonNode tasks) {
		UsageStatistics reference = new UsageStatistics();

		List<JsonNode> converted = new ArrayList<>();
		for (JsonNode child : tasks.getArrayNode()) {
			converted.add(JSONUtility.addChild(child, "statistics", reference.jsonize()));
		}
		return JsonNodeFactories.array(converted);
	}

	private JsonNode internalTaskGroupConversion(JsonNode node) {
		List<JsonNode> converted = new ArrayList<>();
		for (JsonNode child : node.getArrayNode()) {
			JsonNode tasksNode = child.getNode("tasks");
			converted.add(JSONUtility.replaceChild(child, "tasks", internalTaskConversion(tasksNode)));
		}
		return JsonNodeFactories.array(converted);
	}

	@Override
	protected JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion) {
		try {
			JsonNode replacing = previousVersion.getNode("task_groups");
			JsonNode output = JSONUtility.replaceChild(previousVersion, "task_groups", internalTaskGroupConversion(replacing));
			return output.getRootNode();
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

			JsonNode globalHotkey = root.getNode("global_hotkey");
			config.setRECORD(KeyChain.parseJSON(globalHotkey.getArrayNode("record")));
			config.setREPLAY(KeyChain.parseJSON(globalHotkey.getArrayNode("replay")));
			config.setCOMPILED_REPLAY(KeyChain.parseJSON(globalHotkey.getArrayNode("replay_compiled")));

			for (JsonNode compilerNode : root.getArrayNode("compilers")) {
				String name = compilerNode.getStringValue("name");
				String path = compilerNode.getStringValue("path");
				JsonNode compilerSpecificArgs = compilerNode.getNode("compiler_specific_args");

				AbstractNativeCompiler compiler = config.getCompilerFactory().getCompiler(name);
				if (compiler != null) {
					compiler.setPath(new File(path));
					if (!compiler.parseCompilerSpecificArgs(compilerSpecificArgs)) {
						LOGGER.log(Level.WARNING, "Compiler " + name + " was unable to parse its specific arguments.");
					}
				} else {
					throw new IllegalStateException("Unknown compiler " + name);
				}
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
		List<TaskGroup> taskGroups = config.getBackEnd().getTaskGroups();
		for (JsonNode taskGroupNode : root.getArrayNode("task_groups")) {
			TaskGroup taskGroup = TaskGroup.parseJSON(config.getCompilerFactory(), taskGroupNode);
			if (taskGroup != null) {
				taskGroups.add(taskGroup);
			}
		}
		return false;
	}
}