package core.languageHandler.compiler;

import java.io.File;

import core.languageHandler.Language;
import utilities.ILoggable;

public abstract class AbstractNativeCompiler extends AbstractCompiler implements ILoggable {

	@Override
	public final DynamicCompilationResult compile(String source, Language language) {
		if (language != getName()) {
			return DynamicCompilationResult.of(DynamicCompilerOutput.LANGUAGE_NOT_SUPPORTED, null);
		}
		return compile(source);
	}
	public abstract DynamicCompilationResult compile(String source);
	public abstract DynamicCompilationResult compile(String source, File objectFile);
	public abstract Language getName();
	public abstract String getExtension();
	public abstract String getObjectExtension();

	public abstract File getPath();
	public abstract boolean canSetPath();
	public abstract boolean setPath(File path);

	protected abstract File getSourceFile(String compilingAction);
	protected abstract String getDummyPrefix();
}
