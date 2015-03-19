package core;

public abstract class UserDefinedAction {

	protected String name;
	protected int hotkey;
	protected String sourcePath;
	protected String compiler;

	public abstract void action(Core controller) throws InterruptedException;

	public void setName(String name) {
		this.name = name;
	}

	public void setHotkey(int hotkey) {
		this.hotkey = hotkey;
	}

	public String getName() {
		return name;
	}

	public int getHotkey() {
		return hotkey;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public String getCompiler() {
		return compiler;
	}

	public void setCompiler(String compiler) {
		this.compiler = compiler;
	}

}