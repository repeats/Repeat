package core.userDefinedTask.internals.preconditions;

import core.userDefinedTask.UserDefinedAction;
import utilities.natives.processes.NativeProcessUtil;
import utilities.natives.processes.NativeProcessUtil.NativeWindowInfo;

/**
 * Checks whether the execution preconditions are met.
 */
public class ExecutionPreconditionsChecker {

	private NativeWindowInfo cachedActiveWindowInfo;
	private long activeWindowInfoFetchTime;

	public static ExecutionPreconditionsChecker of() {
		return new ExecutionPreconditionsChecker();
	}

	public boolean shouldExecute(UserDefinedAction action) {
		String activeWindowTitle = "";
		String processName = "";

		ActiveWindowsInfoCondition activeWindow = action.getExecutionPreconditions().getActiveWindowCondition();
		if (!action.getExecutionPreconditions().getActiveWindowCondition().isStatic()) {
			NativeWindowInfo activeWindowInfo = getActiveWindowInfo();
			activeWindowTitle = activeWindowInfo.getTitle();
			processName = activeWindowInfo.getProcessName();
		}

		return activeWindow.getProcessNameCondition().isValid(processName) && activeWindow.getTitleCondition().isValid(activeWindowTitle);
	}

	private synchronized NativeWindowInfo getActiveWindowInfo() {
		if (cachedActiveWindowInfo == null || System.currentTimeMillis() - activeWindowInfoFetchTime > 200) {
			cachedActiveWindowInfo = NativeProcessUtil.getActiveWindowInfo();
		}

		return cachedActiveWindowInfo;
	}

	private ExecutionPreconditionsChecker() {}
}
