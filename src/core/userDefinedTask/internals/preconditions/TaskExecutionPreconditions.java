package core.userDefinedTask.internals.preconditions;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import utilities.json.IJsonable;

/**
 * Preconditions for executing an action.
 */
public class TaskExecutionPreconditions implements IJsonable {

	private static final TaskExecutionPreconditions NO_CONDITION = TaskExecutionPreconditions.of(ActiveWindowsInfoCondition.of(AlwaysMatchingStringCondition.INSTANCE, AlwaysMatchingStringCondition.INSTANCE));

	private ActiveWindowsInfoCondition activeWindowCondition;

	public static TaskExecutionPreconditions defaultConditions() {
		return NO_CONDITION;
	}

	public static TaskExecutionPreconditions of(ActiveWindowsInfoCondition activeWindowCondition) {
		TaskExecutionPreconditions result = new TaskExecutionPreconditions();
		result.activeWindowCondition = activeWindowCondition;
		return result;
	}

	public ActiveWindowsInfoCondition getActiveWindowCondition() {
		return activeWindowCondition;
	}

	public static TaskExecutionPreconditions parseJSON(JsonNode node) {
		TaskExecutionPreconditions result = new TaskExecutionPreconditions();
		ActiveWindowsInfoCondition activeWindowCondition = ActiveWindowsInfoCondition.parseJSON(node.getNode("active_window_condition"));
		if (activeWindowCondition == null) {
			return null;
		}
		result.activeWindowCondition = activeWindowCondition;
		return result;
	}

	@Override
	public JsonRootNode jsonize() {
		return JsonNodeFactories.object(JsonNodeFactories.field("active_window_condition", activeWindowCondition.jsonize()));
	}

	private TaskExecutionPreconditions() {}
}
