package core.webui.server.handlers;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import core.ipc.IIPCService;
import core.ipc.IPCServiceManager;
import core.userDefinedTask.TaskGroup;
import core.userDefinedTask.UserDefinedAction;
import frontEnd.MainBackEndHolder;
import utilities.NumberUtility;

public class CommonTask {

	private static final Logger LOGGER = Logger.getLogger(CommonTask.class.getName());

	private CommonTask() {}

	public static IIPCService getIPCService(Map<String, String> params) {
		String indexString = params.get("ipc");
		if (indexString == null || !NumberUtility.isNonNegativeInteger(indexString)) {
			LOGGER.warning("IPC index must be non-negative integer. Got " + indexString + ".");
			return null;
		}

		int index = Integer.parseInt(indexString);
		if (index >= IPCServiceManager.IPC_SERVICE_COUNT) {
			LOGGER.warning("IPC index out of bound: " + index);
			return null;
		}

		return IPCServiceManager.getIPCService(index);
	}

	public static int getTaskIndexFromRequest(MainBackEndHolder backEndHolder, Map<String, String> params, TaskGroup group) {
		String taskValue = params.get("task");
		if (taskValue == null) {
			LOGGER.warning("Missing task.");
			return -1;
		}

		if (!NumberUtility.isNonNegativeInteger(taskValue)) {
			LOGGER.warning("Task indices must be non-negative integers. Got " + taskValue + ".");
			return -1;
		}

		int taskIndex = Integer.parseInt(taskValue);
		List<UserDefinedAction> tasks = group.getTasks();
		if (taskIndex >= tasks.size()) {
			LOGGER.warning("No such task with index " + taskIndex + ".");
			return -1;
		}

		return taskIndex;
	}

	public static UserDefinedAction getTaskFromRequest(MainBackEndHolder backEndHolder, Map<String, String> params) {
		TaskGroup group = getTaskGroupFromRequest(backEndHolder, params, true);
		if (group == null) {
			LOGGER.warning("Cannot get group.");
			return null;
		}

		int taskIndex = getTaskIndexFromRequest(backEndHolder, params, group);
		if (taskIndex == -1) {
			LOGGER.warning("Cannot find task index.");
			return null;
		}

		List<UserDefinedAction> tasks = group.getTasks();
		if (taskIndex >= tasks.size()) {
			LOGGER.warning("No such task with index.");
			return null;
		}

		return tasks.get(taskIndex);
	}

//	public static int getTaskGroupIndexFromRequest(MainBackEndHolder backEndHolder, Map<String, String> params) {
//		String groupValue = params.get("group");
//		if (!NumberUtility.isNonNegativeInteger(groupValue)) {
//			LOGGER.warning("Group index must be non-negative integers. Got " + groupValue + ".");
//			return -1;
//		}
//
//		int groupIndex = Integer.parseInt(groupValue);
//		if (groupIndex >= backEndHolder.getTaskGroups().size()) {
//			LOGGER.warning("Group index out of bound: " + groupIndex);
//			return -1;
//		}
//		return groupIndex;
//	}

	public static String getTaskGroupIdFromRequest(MainBackEndHolder backEndHolder, Map<String, String> params) {
		String groupValue = params.get("group");
		if (groupValue.isEmpty()) {
			LOGGER.warning("Group ID must not be empty.");
			return null;
		}

		return groupValue;
	}

	public static TaskGroup getTaskGroupFromRequest(MainBackEndHolder backEndHolder, Map<String, String> params, boolean useCurrentIfNotProvided) {
		String groupValue = params.get("group");
		if (groupValue == null) {
			if (useCurrentIfNotProvided) {
				return backEndHolder.getCurrentTaskGroup();
			}
			return null;
		}

		String id = getTaskGroupIdFromRequest(backEndHolder, params);
		if (id == null) {
			LOGGER.warning("No such group with ID " + id + ".");
			return null;
		}
		return backEndHolder.getTaskGroup(id);
	}
}
