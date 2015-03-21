package core.languageHandler;

import java.io.File;

import core.UserDefinedAction;

public interface DynamicCompiler {
	public abstract UserDefinedAction compile(String source);
	public abstract String getName();

	public abstract File getPath();
	public abstract void setPath(File path);

	public abstract String getRunArgs();
	public abstract void setRunArgs(String args);
}