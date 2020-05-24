package core.webui.server.handlers;

import core.webui.server.handlers.internals.tasks.TaskSourceCodeFragmentHandler;

public abstract class AbstractTaskSourceCodeHandler extends AbstractSingleMethodHttpHandler {
	protected final TaskSourceCodeFragmentHandler taskSourceCodeFragmentHandler;

	public AbstractTaskSourceCodeHandler(TaskSourceCodeFragmentHandler taskSourceCodeFragmentHandler, String allowedMethod) {
		super(allowedMethod);
		this.taskSourceCodeFragmentHandler = taskSourceCodeFragmentHandler;
	}
}
