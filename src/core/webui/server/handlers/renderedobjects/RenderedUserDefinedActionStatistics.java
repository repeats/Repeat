package core.webui.server.handlers.renderedobjects;

import java.util.Base64;
import java.util.stream.Collectors;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import core.userDefinedTask.UsageStatistics;
import utilities.DateUtility;
import utilities.json.IJsonable;
import utilities.json.JSONUtility;

public class RenderedUserDefinedActionStatistics {

	private static final int MAX_EXECUTION_INSTANCES = 50;

	private String created;
	private String lastUsed;
	private String totalExecutionTime;
	private String averageExecutionTime;
	private String encodedTaskExecutionInstances;

	private RenderedUserDefinedActionStatistics() {}

	public static RenderedUserDefinedActionStatistics fromUserDefinedActionStatistics(UsageStatistics statistics) {
		RenderedUserDefinedActionStatistics result = new RenderedUserDefinedActionStatistics();
		result.created = DateUtility.calendarToTimeString(statistics.getCreated());
		result.lastUsed = statistics.getLastUse() == null ? "Never" : DateUtility.calendarToTimeString(statistics.getLastUse());
		result.totalExecutionTime = DateUtility.durationToString(statistics.getTotalExecutionTime());
		result.averageExecutionTime  = DateUtility.durationToString(Math.round(statistics.getAverageExecutionTime()));
		JsonNode node = JsonNodeFactories.object(
				JsonNodeFactories.field("executionInstances", JsonNodeFactories.array(statistics.getExecutionInstances().stream()
					.skip(Math.max(0, statistics.getExecutionInstances().size() - MAX_EXECUTION_INSTANCES))
					.map(RenderedUserDefinedActionExecutionInstance::fromExecutionInstance)
					.map(IJsonable::jsonize)
					.collect(Collectors.toList()))));
		result.encodedTaskExecutionInstances = Base64.getEncoder().encodeToString(JSONUtility.jsonToString(node).getBytes());
		return result;
	}

	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public String getLastUsed() {
		return lastUsed;
	}
	public void setLastUsed(String lastUsed) {
		this.lastUsed = lastUsed;
	}
	public String getTotalExecutionTime() {
		return totalExecutionTime;
	}
	public void setTotalExecutionTime(String totalExecutionTime) {
		this.totalExecutionTime = totalExecutionTime;
	}
	public String getAverageExecutionTime() {
		return averageExecutionTime;
	}
	public void setAverageExecutionTime(String averageExecutionTime) {
		this.averageExecutionTime = averageExecutionTime;
	}
	public String getEncodedTaskExecutionInstances() {
		return encodedTaskExecutionInstances;
	}
	public void setEncodedTaskExecutionInstances(String encodedTaskExecutionInstances) {
		this.encodedTaskExecutionInstances = encodedTaskExecutionInstances;
	}
}
