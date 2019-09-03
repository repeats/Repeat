package core.languageHandler.compiler;

import java.io.File;

import core.languageHandler.Language;
import core.userDefinedTask.UserDefinedAction;
import utilities.ILoggable;
import utilities.Pair;

public abstract class AbstractNativeCompiler extends AbstractCompiler implements ILoggable {

	@Override
	public final Pair<DynamicCompilerOutput, UserDefinedAction> compile(String source, Language language) {
		if (language != getName()) {
			return Pair.of(DynamicCompilerOutput.LANGUAGE_NOT_SUPPORTED, null);
		}
		return compile(source);
	}
	public abstract Pair<DynamicCompilerOutput, UserDefinedAction> compile(String source);
	public abstract Pair<DynamicCompilerOutput, UserDefinedAction> compile(String source, File objectFile);
	public abstract Language getName();
	public abstract String getExtension();
	public abstract String getObjectExtension();

	public abstract File getPath();
	public abstract boolean canSetPath();
	public abstract boolean setPath(File path);

	protected abstract File getSourceFile(String compilingAction);
	protected abstract String getDummyPrefix();
}
