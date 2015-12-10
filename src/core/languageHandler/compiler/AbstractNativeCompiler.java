package core.languageHandler.compiler;

import java.io.File;

import utilities.ILoggable;
import utilities.Pair;
import argo.jdom.JsonNode;
import core.languageHandler.Language;
import core.userDefinedTask.UserDefinedAction;

public abstract class AbstractNativeCompiler implements ILoggable {

	public abstract Pair<DynamicCompilerOutput, UserDefinedAction> compile(String source);
	public abstract Pair<DynamicCompilerOutput, UserDefinedAction> compile(String source, File objectFile);
	public abstract Language getName();
	public abstract String getExtension();
	public abstract String getObjectExtension();

	public abstract File getPath();
	public abstract void setPath(File path);

	public abstract boolean parseCompilerSpecificArgs(JsonNode node);
	public abstract JsonNode getCompilerSpecificArgs();

	protected abstract File getSourceFile(String compilingAction);
	protected abstract String getDummyPrefix();

}
