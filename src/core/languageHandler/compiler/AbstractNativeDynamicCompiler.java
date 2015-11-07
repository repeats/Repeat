package core.languageHandler.compiler;

import java.io.File;

import argo.jdom.JsonNode;
import core.ILoggable;
import core.userDefinedTask.UserDefinedAction;

public abstract class AbstractNativeDynamicCompiler implements ILoggable {

	public abstract UserDefinedAction compile(String source);
	public abstract UserDefinedAction compile(String source, File objectFile);
	public abstract String getName();
	public abstract String getExtension();
	public abstract String getObjectExtension();

	public abstract File getPath();
	public abstract void setPath(File path);

	public abstract boolean parseCompilerSpecificArgs(JsonNode node);
	public abstract JsonNode getCompilerSpecificArgs();

	protected abstract File getSourceFile(String compilingAction);
	protected abstract String getDummyPrefix();
}