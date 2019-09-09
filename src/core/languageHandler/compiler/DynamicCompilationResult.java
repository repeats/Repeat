package core.languageHandler.compiler;

import core.userDefinedTask.UserDefinedAction;

public class DynamicCompilationResult {
	private DynamicCompilerOutput output;
	private UserDefinedAction action;

	protected DynamicCompilationResult(DynamicCompilerOutput output, UserDefinedAction action) {
		this.output = output;
		this.action = action;
	}

	public static DynamicCompilationResult of(DynamicCompilerOutput output, UserDefinedAction action) {
		return new DynamicCompilationResult(output, action);
	}

	public final DynamicCompilerOutput output() {
		return output;
	}

	public final UserDefinedAction action() {
		return action;
	}
}
