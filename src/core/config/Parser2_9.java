package core.config;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.userDefinedTask.TaskGroup;
import utilities.json.JSONUtility;

public class Parser2_9 extends ConfigParser {

	@Override
	protected String getVersion() {
		return "2.9";
	}

	@Override
	protected String getPreviousVersion() {
		return "2.8";
	}

	@Override
	protected JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion) {
		JsonNode globalConfig = previousVersion.getNode("global_settings");
		globalConfig = JSONUtility.addChild(globalConfig, "tools_config", JsonNodeFactories.array(JsonNodeFactories.string("local"))).getRootNode();
		globalConfig = JSONUtility.addChild(globalConfig, "core_config", JsonNodeFactories.array(JsonNodeFactories.string("local"))).getRootNode();
		previousVersion = JSONUtility.replaceChild(previousVersion, "global_settings", globalConfig).getRootNode();
		previousVersion = JSONUtility.addChild(previousVersion, "remote_repeats_clients",
				JsonNodeFactories.object(
						JsonNodeFactories.field(JsonNodeFactories.string("clients"), JsonNodeFactories.array()))).getRootNode();

		JsonNode compilers = previousVersion.getNode("compilers");
		compilers = JsonNodeFactories.object(
				JsonNodeFactories.field("local_compilers", compilers),
				JsonNodeFactories.field("remote_repeats_compilers", JsonNodeFactories.array(JsonNodeFactories.string("local")))
				);
		previousVersion = JSONUtility.replaceChild(previousVersion, "compilers", compilers).getRootNode();

		List<JsonNode> groups = previousVersion.getArrayNode("task_groups");
		List<JsonNode> newGroups = new ArrayList<>();
		for (JsonNode group : groups) {
			List<JsonNode> tasks = group.getArrayNode("tasks");
			List<JsonNode> newTasks = new ArrayList<>();
			for (JsonNode task : tasks) {
				JsonNode taskWithId = JSONUtility.addChild(task, "action_id", JsonNodeFactories.string(UUID.randomUUID().toString()));
				newTasks.add(taskWithId);
			}

			JsonNode newGroup = JSONUtility.replaceChild(group, "tasks", JsonNodeFactories.array(newTasks));
			newGroup = JSONUtility.addChild(newGroup, "group_id", JsonNodeFactories.string(UUID.randomUUID().toString()));
			newGroups.add(newGroup);
		}

		return JSONUtility.replaceChild(previousVersion, "task_groups", JsonNodeFactories.array(newGroups)).getRootNode();
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
}