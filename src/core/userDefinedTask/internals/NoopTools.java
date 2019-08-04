package core.userDefinedTask.internals;

import java.io.File;

public class NoopTools implements ITools {

	private static final NoopTools INSTANCE = new NoopTools();

	private NoopTools() {}

	public static NoopTools of() {
		return INSTANCE;
	}

	@Override
	public String getClipboard() {
		return "";
	}

	@Override
	public boolean setClipboard(String data) {
		return true;
	}

	@Override
	public String execute(String command) {
		return "";
	}

	@Override
	public String execute(String command, File cwd) {
		return "";
	}
}
