package core.userDefinedTask.internals;

public class RunActionConfig {
	private int repeatCount;
	private long delayMsBetweenRepeats;

	private RunActionConfig() {}

	public static RunActionConfig of() {
		return of(1, 0);
	}

	public static RunActionConfig of(int repeatCount, long delayMsBetweenRepeats) {
		RunActionConfig result = new RunActionConfig();
		result.repeatCount = repeatCount;
		result.delayMsBetweenRepeats = delayMsBetweenRepeats;
		return result;
	}

	public int getRepeatCount() {
		return repeatCount;
	}
	public long getDelayMsBetweenRepeats() {
		return delayMsBetweenRepeats;
	}
}
