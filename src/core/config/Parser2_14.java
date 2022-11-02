package core.config;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.userDefinedTask.TaskGroup;
import utilities.json.JSONUtility;

public class Parser2_14 extends ConfigParser {

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