package core.languageHandler.compiler;

import core.userDefinedTask.UserDefinedAction;

public class DynamicCompilationResult {
	private DynamicCompilerOutput output;
	private UserDefinedAction action;

	private DynamicCompilationResult(DynamicCompilerOutput output, UserDefinedAction action) {
		this.output = output;
		this.action = action;
	}

	public static DynamicCompilationResult of(DynamicCompilerOutput output, UserDefinedAction action) {
		return new DynamicCompilationResult(output, action);
	}

	public DynamicCompilerOutput output() {
		return output;
	}

	public UserDefinedAction action() {
		return action;
	}
}
