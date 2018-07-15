package core.webui.server.handlers;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import core.userDefinedTask.TaskGroup;
import core.userDefinedTask.UserDefinedAction;
import frontEnd.MainBackEndHolder;
import utilities.NumberUtility;

public class CommonTask {

	private static final Logger LOGGER = Logger.getLogger(CommonTask.class.getName());

	private CommonTask() {}

	public static UserDefinedAction getTaskFromRequest(MainBackEndHolder backEndHolder, Map<String, String> params) {
		TaskGroup group = getTaskGroup(backEndHolder, params);
		if (group == null) {
			LOGGER.warning("Cannot get group.");
			return null;
		}

		String taskValue = params.get("task");
		if (taskValue == null) {
			LOGGER.warning("Missing task.");
			return null;
		}

		if (!NumberUtility.isNonNegativeInteger(taskValue)) {
			LOGGER.warning("Group and task indices must be positive integers.");
			return null;
		}

		int taskIndex = Integer.parseInt(taskValue);
		List<UserDefinedAction> tasks = group.getTasks();
		if (taskIndex >= tasks.size()) {
			LOGGER.warning("No such task with index.");
			return null;
		}

		return tasks.get(taskIndex);
	}

	private static TaskGroup getTaskGroup(MainBackEndHolder backEndHolder, Map<String, String> params) {
		String groupValue = params.get("group");
		if (groupValue == null) {
			return backEndHolder.getCurrentTaskGroup();
		}

		if (!NumberUtility.isNonNegativeInteger(groupValue)) {
			LOGGER.warning("Group index must be positive integers.");
			return null;
		}

		int groupIndex = Integer.parseInt(groupValue);
		List<TaskGroup> groups = backEndHolder.getTaskGroups();
		if (groupIndex >= groups.size()) {
			LOGGER.warning("No such group with index.");
			return null;
		}

		return groups.get(groupIndex);
	}
}
