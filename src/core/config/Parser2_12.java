package core.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.userDefinedTask.TaskGroup;
import utilities.json.JSONUtility;

public class Parser2_12 extends ConfigParser {

	@Override
	protected String getVersion() {
		return "2.12";
	}

	@Override
	protected String getPreviousVersion() {
		return "2.11";
	}

	@Override
	protected JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion) {
		JsonNode converted = previousVersion;
		converted = JSONUtility.replaceChild(converted, "global_settings", convertGlobalSettings(previousVersion.getNode("global_settings")));

		List<JsonNode> taskGroups = previousVersion.getArrayNode("task_groups");
		List<JsonNode> replacedGroups = new ArrayList<>(taskGroups.size());
		for (JsonNode group : taskGroups) {
			List<JsonNode> tasks = group.getArrayNode("tasks");
			List<JsonNode> replacedTasks = tasks.stream().map(t -> convertTask(t)).collect(Collectors.toList());

			JsonNode replacedGroup = JSONUtility.replaceChild(group, "tasks", JsonNodeFactories.array(replacedTasks));
			replacedGroups.add(replacedGroup);
		}

		JsonNode newTaskGroups = JsonNodeFactories.array(replacedGroups);
		return JSONUtility.replaceChild(converted, "task_groups", newTaskGroups).getRootNode();
	}

	private JsonNode convertGlobalSettings(JsonNode globalSettings) {
		JsonNode globalHotKey = globalSettings.getNode("global_hotkey");
		Map<String, JsonNode> replaces = new HashMap<>();
		replaces.put("record", convertKeyStrokes(globalHotKey.getNode("record")));
		replaces.put("replay", convertKeyStrokes(globalHotKey.getNode("replay")));
		replaces.put("replay_compiled", convertKeyStrokes(globalHotKey.getNode("replay_compiled")));
		JsonNode replacedGlobalHotKey = JSONUtility.replaceChildren(globalHotKey, replaces);

		return JSONUtility.replaceChild(globalSettings, "global_hotkey", replacedGlobalHotKey);
	}

	private JsonNode convertTask(JsonNode task) {
		JsonNode activation = task.getNode("activation");
		task = JSONUtility.replaceChild(task, "activation", convertActivation(activation));

		JsonNode statistics = task.getNode("statistics");
		return JSONUtility.replaceChild(task, "statistics", convertStatistics(statistics));
	}

	private JsonNode convertStatistics(JsonNode statistics) {
		JsonNode breakdown = statistics.getNode("task_activations_breakdown");
		List<JsonNode> breakdowns = breakdown.getArrayNode();
		List<JsonNode> replacedBreakdowns = new ArrayList<>(breakdowns.size());
		for (JsonNode entry : breakdowns) {
			JsonNode replacedActivation = convertActivation(entry.getNode("task_activation"));
			replacedBreakdowns.add(JSONUtility.replaceChild(entry, "task_activation", replacedActivation));
		}
		return JSONUtility.replaceChild(statistics, "task_activations_breakdown", JsonNodeFactories.array(replacedBreakdowns));
	}

	private JsonNode convertActivation(JsonNode activation) {
		List<JsonNode> hotKeys = activation.getArrayNode("hotkey");
		List<JsonNode> replacedHotkeys = hotKeys.stream().map(hotKey -> convertKeyStrokes(hotKey)).collect(Collectors.toList());
		activation = JSONUtility.replaceChild(activation, "hotkey", JsonNodeFactories.array(replacedHotkeys));

		List<JsonNode> keySequences = activation.getArrayNode("key_sequence");
		List<JsonNode> replacedKeySequence = keySequences.stream().map(keySequence -> convertKeyStrokes(keySequence)).collect(Collectors.toList());
		activation = JSONUtility.replaceChild(activation, "key_sequence", JsonNodeFactories.array(replacedKeySequence));

		return activation;
	}

	private JsonNode convertKeyStrokes(JsonNode keyStrokesNode) {
		List<JsonNode> strokes = keyStrokesNode.getArrayNode();
		List<JsonNode> replacedStrokes = strokes.stream().map(s -> convertKeyStroke(s)).collect(Collectors.toList());
		return JsonNodeFactories.array(replacedStrokes);
	}

	private JsonNode convertKeyStroke(JsonNode keyStroke) {
		return JSONUtility.addChild(keyStroke, "type", JsonNodeFactories.string("key_stroke"));
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