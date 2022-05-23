package core.webui.server.handlers.renderedobjects;

import java.util.List;
import java.util.stream.Collectors;

import core.userDefinedTask.internals.TaskSourceHistory;

public class RenderedTaskSourceHistory {

	private List<RenderedTaskSourceHistoryEntry> entries;

	public static RenderedTaskSourceHistory of(String taskId, TaskSourceHistory history) {
		RenderedTaskSourceHistory result = new RenderedTaskSourceHistory();
		result.entries = history.getEntries().stream().map(e -> RenderedTaskSourceHistoryEntry.of(taskId, e)).collect(Collectors.toList());
		return result;
	}

	public List<RenderedTaskSourceHistoryEntry> getEntries() {
		return entries;
	}
}
