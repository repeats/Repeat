package core.webui.server.handlers.renderedobjects;

import core.userDefinedTask.internals.preconditions.TaskExecutionPreconditions;

public class RenderedTaskExecutionPreconditions {
	private RenderedActiveWindowInfosPreconditions activeWindowInfosPreconditions;

	public static RenderedTaskExecutionPreconditions of(TaskExecutionPreconditions preconditions) {
		RenderedTaskExecutionPreconditions result = new RenderedTaskExecutionPreconditions();
		result.activeWindowInfosPreconditions = RenderedActiveWindowInfosPreconditions.of(preconditions.getActiveWindowCondition());
		return result;
	}

	public RenderedActiveWindowInfosPreconditions getActiveWindowInfosPreconditions() {
		return activeWindowInfosPreconditions;
	}
	public void setActiveWindowInfosPreconditions(RenderedActiveWindowInfosPreconditions activeWindowInfosPreconditions) {
		this.activeWindowInfosPreconditions = activeWindowInfosPreconditions;
	}

	private RenderedTaskExecutionPreconditions() {}
}
