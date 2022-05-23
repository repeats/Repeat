package core.userDefinedTask.internals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** Contains information about the source history of a task. */
public class TaskSourceHistory {

	private List<TaskSourceHistoryEntry> entries;

	public TaskSourceHistory() {
		this.entries = new ArrayList<>();
	}

	/**
	 * Finds a particular entry given the timestamp.
	 *
	 * @param timestamp the timestamp to find the entry for.
	 * @return the found entry, or null if no entry was found.
	 */
	public TaskSourceHistoryEntry findEntry(long timestamp) {
		for (TaskSourceHistoryEntry entry : entries) {
			if (entry.getCreated().getTimeInMillis() == timestamp) {
				return entry;
			}
		}
		return null;
	}

	// Adds a single entry to the history.
	public void addEntry(TaskSourceHistoryEntry entry) {
		this.entries.add(entry);
	}

	/// Adds all history to this history.
	public void addHistory(TaskSourceHistory history) {
		entries.addAll(history.entries);
		entries.sort((e1, e2) -> e2.getCreated().compareTo(e1.getCreated()));
	}

	// Returns the list of entries sorted in reverse chronological order.
	public List<TaskSourceHistoryEntry> getEntries() {
		return entries.stream().sorted((e1, e2) -> e2.getCreated().compareTo(e1.getCreated())).collect(Collectors.toList());
	}
}
