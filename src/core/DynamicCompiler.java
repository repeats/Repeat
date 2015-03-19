package core;

public interface DynamicCompiler {
	public abstract UserDefinedAction compile(String source);
	public abstract String getName();
}