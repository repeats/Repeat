package core.webui.server.handlers.renderedobjects;

import core.userDefinedTask.UsageStatistics.ExecutionInstance;
import utilities.DateUtility;
import utilities.json.AutoJsonable;

public class RenderedUserDefinedActionExecutionInstance extends AutoJsonable {
	private String start;
	private String end;
	private long duration;
	private String hasEnding;

	public static RenderedUserDefinedActionExecutionInstance fromExecutionInstance(ExecutionInstance instance) {
		RenderedUserDefinedActionExecutionInstance result = new RenderedUserDefinedActionExecutionInstance();
		result.start = DateUtility.calendarToDateString(DateUtility.calendarFromMillis(instance.getStart()));
		result.start = DateUtility.calendarToDateString(DateUtility.calendarFromMillis(instance.getEnd()));
		result.duration = instance.getDuration();
		result.hasEnding = instance.getEnd() != ExecutionInstance.DID_NOT_END ? true + "" : false + "";

		return result;
	}

	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public String getHasEnding() {
		return hasEnding;
	}
	public void setHasEnding(String hasEnding) {
		this.hasEnding = hasEnding;
	}
}
