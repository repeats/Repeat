package core.webui.server.handlers.renderedobjects;

import core.userDefinedTask.internals.TaskSourceHistoryEntry;
import utilities.DateUtility;

public class RenderedTaskSourceHistoryEntry {
	private String taskId;
	private String createdTime;
	private String createdTimeMillis;

	public static RenderedTaskSourceHistoryEntry of(String taskId, TaskSourceHistoryEntry entry) {
		RenderedTaskSourceHistoryEntry result = new RenderedTaskSourceHistoryEntry();
		result.taskId = taskId;
		result.createdTime = DateUtility.calendarToTimeString(entry.getCreated());
		result.createdTimeMillis = entry.getCreated().getTimeInMillis() + "";
		return result;
	}

	public String getTaskId() {
		return taskId;
	}
	public String getCreatedTime() {
		return createdTime;
	}
	public String getCreatedTimeMillis() {
		return createdTimeMillis;
	}
}
