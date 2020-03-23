package core.userDefinedTask.internals;

import core.keyChain.TaskActivation;

public class ActionExecutionRequest {
	private int repeatCount;
	private long delayMsBetweenRepeat;
	private TaskActivation activation;

	private ActionExecutionRequest() {}

	public static ActionExecutionRequest of() {
		return of(1, 0);
	}

	public static ActionExecutionRequest of(int repeatCount, long delay) {
		ActionExecutionRequest result = new ActionExecutionRequest();
		result.repeatCount = repeatCount;
		result.delayMsBetweenRepeat = delay;
		result.activation = null;
		return result;
	}

	public int getRepeatCount() {
		return repeatCount;
	}
	public long getDelayMsBetweenRepeat() {
		return delayMsBetweenRepeat;
	}
	public TaskActivation getActivation() {
		return activation;
	}
}
