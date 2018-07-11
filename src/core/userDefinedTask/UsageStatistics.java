package core.userDefinedTask;

import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import utilities.DateUtility;
import utilities.ILoggable;
import utilities.json.IJsonable;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;

public class UsageStatistics implements IJsonable, ILoggable {

	private static final Logger LOGGER = Logger.getLogger(UsageStatistics.class.getName());

	private long count;
	private Calendar lastUse;
	private Calendar created;
	private long totalExecutionTime;

	public UsageStatistics() {
		created = Calendar.getInstance();
	}

	@Override
	public JsonRootNode jsonize() {
		return JsonNodeFactories.object(
				JsonNodeFactories.field("count", JsonNodeFactories.number(count)),
				JsonNodeFactories.field("total_execution_time", JsonNodeFactories.number(totalExecutionTime)),
				JsonNodeFactories.field("last_use", lastUse != null ? JsonNodeFactories.string(DateUtility.calendarToTimeString(lastUse)) : JsonNodeFactories.nullNode()),
				JsonNodeFactories.field("created", JsonNodeFactories.string(DateUtility.calendarToTimeString(created)))
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

			UsageStatistics output = new UsageStatistics();
			output.count = count;
			output.totalExecutionTime = totalExecutionTime;
			output.lastUse = lastUse;
			output.created = created;

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

	public void useNow() {
		if (lastUse == null) {
			lastUse = Calendar.getInstance();
		} else {
			lastUse.setTimeInMillis(System.currentTimeMillis());
		}
	}

	public Calendar getCreated() {
		return created;
	}

	public void createNow() {
		created.setTimeInMillis(System.currentTimeMillis());
	}

	public double getAverageExecutionTime() {
		return (double) totalExecutionTime / count;
	}

	public long getTotalExecutionTime() {
		return totalExecutionTime;
	}

	public void updateAverageExecutionTime(long newExecutionTime) {
		count++;
		totalExecutionTime += newExecutionTime;
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(UsageStatistics.class.getName());
	}
}
