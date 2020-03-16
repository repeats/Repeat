package core.userDefinedTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import utilities.DateUtility;
import utilities.json.AutoJsonable;
import utilities.json.IJsonable;
import utilities.json.JSONUtility;
import utilities.json.Jsonizer;

public class UsageStatistics implements IJsonable {

	private static final Logger LOGGER = Logger.getLogger(UsageStatistics.class.getName());

	private long count;
	private Calendar lastUse;
	private Calendar created;
	private long totalExecutionTime;
	private Map<String, ExecutionInstance> onGoingInstances;
	private List<ExecutionInstance> executionInstances;

	public UsageStatistics() {
		created = Calendar.getInstance();
		onGoingInstances = new HashMap<>();
		executionInstances = new ArrayList<>();
	}

	@Override
	public JsonRootNode jsonize() {
		return JsonNodeFactories.object(
				JsonNodeFactories.field("count", JsonNodeFactories.number(count)),
				JsonNodeFactories.field("total_execution_time", JsonNodeFactories.number(totalExecutionTime)),
				JsonNodeFactories.field("last_use", lastUse != null ? JsonNodeFactories.string(DateUtility.calendarToTimeString(lastUse)) : JsonNodeFactories.nullNode()),
				JsonNodeFactories.field("created", JsonNodeFactories.string(DateUtility.calendarToTimeString(created))),
				JsonNodeFactories.field("execution_instances", JsonNodeFactories.array(JSONUtility.listToJson(executionInstances)))
				);
	}

	public static UsageStatistics parseJSON(JsonNode node) {
		try {
			long count = Long.parseLong(node.getNumberValue("count"));
			long totalExecutionTime = Long.parseLong(node.getNumberValue("total_execution_time"));

			Calendar lastUse;
			if (node.isNullableObjectNode("last_use")) {
				lastUse = null;
			} else {
				lastUse = DateUtility.stringToCalendar(node.getStringValue("last_use"));
			}

			Calendar created = DateUtility.stringToCalendar(node.getStringValue("created"));
			if (created == null) {
				LOGGER.warning("Unable to parse object.");
				return null;
			}

			List<ExecutionInstance> instances = new ArrayList<>();
			if (node.isArrayNode("execution_instances")) {
				List<JsonNode> nodes = node.getArrayNode("execution_instances");
				instances = nodes.stream().map(n -> {
					ExecutionInstance i = ExecutionInstance.of(0, 0);
					Jsonizer.parse(n, i);
					return i;
				}).collect(Collectors.toList());
			}

			UsageStatistics output = new UsageStatistics();
			output.count = count;
			output.totalExecutionTime = totalExecutionTime;
			output.lastUse = lastUse;
			output.created = created;
			output.executionInstances = instances;

			return output;
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Encountered exception when parsing usage statistics", e);
			return null;
		}
	}

	public long getCount() {
		return count;
	}

	public Calendar getLastUse() {
		return lastUse;
	}

	public Calendar getCreated() {
		return created;
	}

	public double getAverageExecutionTime() {
		return (double) totalExecutionTime / count;
	}

	public long getTotalExecutionTime() {
		return totalExecutionTime;
	}

	public List<ExecutionInstance> getExecutionInstances() {
		return Collections.unmodifiableList(executionInstances);
	}

	/**
	 * @return an ID to update at completion time.
	 */
	public synchronized String useNow() {
		if (lastUse == null) {
			lastUse = Calendar.getInstance();
		} else {
			lastUse.setTimeInMillis(System.currentTimeMillis());
		}

		String id = UUID.randomUUID().toString();
		ExecutionInstance instance = ExecutionInstance.of(System.currentTimeMillis(), ExecutionInstance.DID_NOT_END);
		onGoingInstances.put(id, instance);
		executionInstances.add(instance);
		return id;
	}

	public void createNow() {
		created.setTimeInMillis(System.currentTimeMillis());
	}

	public synchronized void executionFinished(String id) {
		count++;
		if (!onGoingInstances.containsKey(id)) {
			LOGGER.warning("Unable to find start time for execution statistics " + id);
			return;
		}

		ExecutionInstance instance = onGoingInstances.remove(id);
		long start = instance.getStart();
		long end = System.currentTimeMillis();
		instance.setEnd(end);

		totalExecutionTime += end-start;
	}

	public static class ExecutionInstance extends AutoJsonable {
		public static final Long DID_NOT_END = -1L;

		private long start;
		private long end;

		public static ExecutionInstance of(long start, long end) {
			return new ExecutionInstance(start, end);
		}

		private ExecutionInstance(long start, long end) {
			this.start = start;
			this.end = end;
		}

		public long getStart() {
			return start;
		}

		public long getEnd() {
			return end;
		}

		public long getDuration() {
			return end - start;
		}

		private void setEnd(long end) {
			this.end = end;
		}
	}
}
