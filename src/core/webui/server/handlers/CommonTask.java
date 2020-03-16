package core.webui.server.handlers;

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

	public static String getTaskIdFromRequest(MainBackEndHolder backEndHolder, Map<String, String> params) {
		String taskValue = params.get("task");
		if (taskValue == null || taskValue.isEmpty()) {
			LOGGER.warning("Missing task ID.");
			return "";
		}

		return taskValue;
	}

	public static UserDefinedAction getTaskFromRequest(MainBackEndHolder backEndHolder, Map<String, String> params) {
		String taskId = getTaskIdFromRequest(backEndHolder, params);
		if (taskId == null || taskId.isEmpty()) {
			LOGGER.warning("Cannot find task ID.");
			return null;
		}

		UserDefinedAction task = backEndHolder.getTask(taskId);
		if (task == null) {
			LOGGER.warning("No such task with ID " + taskId + ".");
			return null;
		}

		return task;
	}

	public static String getTaskGroupIdFromRequest(MainBackEndHolder backEndHolder, Map<String, String> params) {
		String groupValue = params.get("group");
		if (groupValue == null || groupValue.isEmpty()) {
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
