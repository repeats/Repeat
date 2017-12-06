package core.languageHandler.compiler;

public enum DynamicCompilerOutput {
	COMPILATION_SUCCESS,
	SOURCE_NOT_ACCESSIBLE, // e.g. Permission denied
	SOURCE_MISSING_PREFORMAT_ELEMENTS,
	COMPILER_MISSING,
	COMPILER_MISCONFIGURED,
	COMPILATION_ERROR,
	CONSTRUCTOR_ERROR, // Missing nullary constructor.
}
