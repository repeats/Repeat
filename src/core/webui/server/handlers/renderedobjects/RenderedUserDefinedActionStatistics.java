package core.webui.server.handlers.renderedobjects;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import core.keyChain.TaskActivation;
import core.userDefinedTask.UsageStatistics;
import utilities.DateUtility;
import utilities.Pair;
import utilities.json.IJsonable;
import utilities.json.JSONUtility;

public class RenderedUserDefinedActionStatistics {

	private static final int MAX_EXECUTION_INSTANCES = 100;

	private String created;
	private String lastUsed;
	private String totalExecutionTime;
	private String averageExecutionTime;
	private String encodedTaskActivationBreakdown;
	private String encodedTaskExecutionInstances;

	private RenderedUserDefinedActionStatistics() {}

	public static RenderedUserDefinedActionStatistics fromUserDefinedActionStatistics(UsageStatistics statistics) {
		RenderedUserDefinedActionStatistics result = new RenderedUserDefinedActionStatistics();
		result.created = DateUtility.calendarToTimeString(statistics.getCreated());
		result.lastUsed = statistics.getLastUse() == null ? "Never" : DateUtility.calendarToTimeString(statistics.getLastUse());
		result.totalExecutionTime = DateUtility.durationToString(statistics.getTotalExecutionTime());
		result.averageExecutionTime  = DateUtility.durationToString(Math.round(statistics.getAverageExecutionTime()));

		List<String> activations = new ArrayList<>(statistics.getTaskActivationBreakdown().size());
		List<Long> activationCount = new ArrayList<>(statistics.getTaskActivationBreakdown().size());
		for (Entry<TaskActivation, Long> entry : statistics.getTaskActivationBreakdown().entrySet()) {
			activations.add(entry.getKey().getRepresentativeString());
			activationCount.add(entry.getValue());
		}

		JsonNode taskActivationBreakdownNode = prepareTaskActivationBreakdown(statistics);
		result.encodedTaskActivationBreakdown = Base64.getEncoder().encodeToString(JSONUtility.jsonToString(taskActivationBreakdownNode).getBytes());

		JsonNode executionInstancesNode = JsonNodeFactories.object(
				JsonNodeFactories.field("executionInstances", JsonNodeFactories.array(statistics.getExecutionInstances().stream()
					.skip(Math.max(0, statistics.getExecutionInstances().size() - MAX_EXECUTION_INSTANCES))
					.map(RenderedUserDefinedActionExecutionInstance::fromExecutionInstance)
					.map(IJsonable::jsonize)
					.collect(Collectors.toList()))));
		result.encodedTaskExecutionInstances = Base64.getEncoder().encodeToString(JSONUtility.jsonToString(executionInstancesNode).getBytes());
		return result;
	}


	private static final List<Color> BREAKDOWN_COLORS = Arrays.asList(
			Color.RED,
			Color.GREEN,
			Color.BLUE,
			Color.MAGENTA,
			new Color(128,0,0), // Maroon
			new Color(255, 215, 0), // Gold
			new Color(0, 0, 128), // Navy
			new Color(250, 128, 114), // Salmon
			Color.GRAY,
			Color.BLACK
			);

	private static final class BreakdownPieChartEntry {
		String name;
		String color;
		long data;

		private static BreakdownPieChartEntry of(String name, String color, long data) {
			BreakdownPieChartEntry result = new BreakdownPieChartEntry();
			result.name = name.isEmpty() ? "Empty" : name;
			result.color = color;
			result.data = data;
			return result;
		}
	}

	private static JsonNode prepareTaskActivationBreakdown(UsageStatistics statistics) {
		List<Pair<String, Long>> sortedData = statistics.getTaskActivationBreakdown().entrySet().stream()
			.map(e -> Pair.of(e.getKey().getRepresentativeString(), e.getValue()))
			.sorted((e1, e2) -> e2.getB().compareTo(e1.getB())) // Largest one first.
			.collect(Collectors.toList());

		if (sortedData.size() > BREAKDOWN_COLORS.size()) {
			long otherCount = sortedData.stream().skip(BREAKDOWN_COLORS.size() - 1).map(e -> e.getB()).reduce(0L, Long::sum);
			sortedData = Stream.concat(
						sortedData.stream().limit(BREAKDOWN_COLORS.size() - 1),
						Stream.of(Pair.of("Other", otherCount)))
					.collect(Collectors.toList());
		}

		List<BreakdownPieChartEntry> data = new ArrayList<>(sortedData.size());
		for (ListIterator<Pair<String, Long>> iterator = sortedData.listIterator(); iterator.hasNext();) {
			int i = iterator.nextIndex();
			Pair<String, Long> e = iterator.next();

			data.add(BreakdownPieChartEntry.of(e.getA(), formatColor(BREAKDOWN_COLORS.get(i)), e.getB()));
		}

		return taskActivationBreakdownFromData(data);
	}

	private static String formatColor(Color c) {
		return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
	}

	private static JsonNode taskActivationBreakdownFromData(List<BreakdownPieChartEntry> counts) {
		return JsonNodeFactories.object(JsonNodeFactories.field("taskActivationBreakdown",
				JsonNodeFactories.object(
						JsonNodeFactories.field("activations", JsonNodeFactories.array(
								counts.stream().map(c -> JsonNodeFactories.string(c.name)).collect(Collectors.toList()))),
						JsonNodeFactories.field("colors", JsonNodeFactories.array(
								counts.stream().map(c -> JsonNodeFactories.string(c.color)).collect(Collectors.toList()))),
						JsonNodeFactories.field("values", JsonNodeFactories.array(
								counts.stream().map(c -> JsonNodeFactories.number(c.data)).collect(Collectors.toList()))))));
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
	public String getEncodedTaskActivationBreakdown() {
		return encodedTaskActivationBreakdown;
	}
	public void setEncodedTaskActivationBreakdown(String encodedTaskActivationBreakdown) {
		this.encodedTaskActivationBreakdown = encodedTaskActivationBreakdown;
	}
	public String getEncodedTaskExecutionInstances() {
		return encodedTaskExecutionInstances;
	}
	public void setEncodedTaskExecutionInstances(String encodedTaskExecutionInstances) {
		this.encodedTaskExecutionInstances = encodedTaskExecutionInstances;
	}
}
