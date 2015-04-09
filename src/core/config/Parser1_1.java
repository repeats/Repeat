package core.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.KeyChain;
import core.TaskGroup;
import core.languageHandler.compiler.DynamicCompiler;

public class Parser1_1 extends ConfigParser {

	private static final Logger LOGGER = Logger.getLogger(Parser1_1.class.getName());

	@Override
	protected String getVersion() {
		return "1.1";
	}

	@Override
	protected String getPreviousVersion() {
		return "1.0";
	}

	@Override
	protected JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion) {
		try {
			List<JsonNode> newTask = new ArrayList<>();
			for (JsonNode oldTask : previousVersion.getArrayNode("tasks")) {
				newTask.add(JsonNodeFactories.object(
							JsonNodeFactories.field("source_path", oldTask.getNode("source_path")),
							JsonNodeFactories.field("compiler", oldTask.getNode("compiler")),
							JsonNodeFactories.field("name", oldTask.getNode("name")),
							JsonNodeFactories.field("hotkey", oldTask.getNode("hotkey")),
							JsonNodeFactories.field("enabled", JsonNodeFactories.booleanNode(true))
						));
			}
			JsonNode onlyGroup = JsonNodeFactories.array(JsonNodeFactories.object(
					JsonNodeFactories.field("name", JsonNodeFactories.string("default")),
					JsonNodeFactories.field("enabled", JsonNodeFactories.booleanNode(true)),
					JsonNodeFactories.field("tasks", JsonNodeFactories.array(newTask))
					));


			JsonRootNode newRoot = JsonNodeFactories.object(
					JsonNodeFactories.field("version", JsonNodeFactories.string(getVersion())),
					JsonNodeFactories.field("global_hotkey", previousVersion.getNode("global_hotkey")),
					JsonNodeFactories.field("compilers", previousVersion.getNode("compilers")),
					JsonNodeFactories.field("task_groups", onlyGroup)
					);

			return newRoot;
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to convert json from previous version " + getPreviousVersion(), e);
			return null;
		}
	}

	@Override
	protected boolean internalExtractData(Config config, JsonRootNode root) {
		try {
			JsonNode globalHotkey = root.getNode("global_hotkey");
			config.setRECORD(KeyChain.parseJSON(globalHotkey.getArrayNode("record")));
			config.setREPLAY(KeyChain.parseJSON(globalHotkey.getArrayNode("replay")));
			config.setCOMPILED_REPLAY(KeyChain.parseJSON(globalHotkey.getArrayNode("replay_compiled")));

			for (JsonNode compilerNode : root.getArrayNode("compilers")) {
				String name = compilerNode.getStringValue("name");
				String path = compilerNode.getStringValue("path");
				String runArgs = compilerNode.getStringValue("run_args");

				DynamicCompiler compiler = config.compilerFactory().getCompiler(name);
				if (compiler != null) {
					compiler.setPath(new File(path));
					compiler.setRunArgs(runArgs);
				} else {
					throw new IllegalStateException("Unknown compiler " + name);
				}
			}

			List<TaskGroup> taskGroups = config.getBackEnd().getTaskGroups();
			taskGroups.clear();
			for (JsonNode taskGroupNode : root.getArrayNode("task_groups")) {
				TaskGroup taskGroup = TaskGroup.parseJSON(config.compilerFactory(), taskGroupNode);
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
}