package core.userDefinedTask.internals;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import utilities.json.IJsonable;

/** Contains information about the source history of a task. */
public class TaskSourceHistory implements IJsonable {

	private static final Logger LOGGER = Logger.getLogger(TaskSourceHistory.class.getName());

	private static final int MAX_REVISION_COUNT = 10;

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
		entries.add(entry);
		entries.sort((e1, e2) -> e2.getCreated().compareTo(e1.getCreated()));
		if (entries.size() > MAX_REVISION_COUNT) {
			// Remove the last one, which is the oldest one.
			entries.remove(entries.size() - 1);
		}
	}

	/// Adds all history to this history.
	public void addHistory(TaskSourceHistory history) {
		entries.addAll(history.entries);
		entries.sort((e1, e2) -> e2.getCreated().compareTo(e1.getCreated()));
		while (entries.size() > MAX_REVISION_COUNT) {
			// Remove the last one, which is the oldest one.
			entries.remove(entries.size() - 1);
		}
	}

	// Returns the list of entries sorted in reverse chronological order.
	public List<TaskSourceHistoryEntry> getEntries() {
		return entries.stream().sorted((e1, e2) -> e2.getCreated().compareTo(e1.getCreated())).collect(Collectors.toList());
	}

	public static TaskSourceHistory parseJSON(JsonNode node) {
		if (!node.isArrayNode("entries")) {
			LOGGER.warning("Unable to retrieve task source history entries.");
			return null;
		}

		List<TaskSourceHistoryEntry> entries = new ArrayList<>();
		for (JsonNode entry : node.getArrayNode("entries")) {
			String path = entry.getStringValue("path");
			long createdTime = Long.parseLong(entry.getNumberValue("created_time"));
			entries.add(TaskSourceHistoryEntry.of(path, createdTime));
		}

		TaskSourceHistory result = new TaskSourceHistory();
		result.entries = entries;
		return result;
	}

	@Override
	public JsonRootNode jsonize() {
		return JsonNodeFactories.object(JsonNodeFactories.field("entries", JsonNodeFactories.array(
				entries.stream().map(
						e -> JsonNodeFactories.object(
								JsonNodeFactories.field("path", JsonNodeFactories.string(e.getSourcePath())),
								JsonNodeFactories.field("created_time", JsonNodeFactories.number(e.getCreated().getTimeInMillis()))
								).getNode())
				.collect(Collectors.toList()))
				));
	}
}
