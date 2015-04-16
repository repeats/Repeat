package core.languageHandler.compiler;

import java.io.File;

import core.userDefinedTask.UserDefinedAction;

public interface DynamicCompiler {
	public abstract UserDefinedAction compile(String source);
	public abstract UserDefinedAction compile(String source, File objectFile);
	public abstract String getName();
	public abstract String getExtension();
	public abstract String getObjectExtension();

	public abstract File getPath();
	public abstract void setPath(File path);

	public abstract String getRunArgs();
	public abstract void setRunArgs(String args);
}