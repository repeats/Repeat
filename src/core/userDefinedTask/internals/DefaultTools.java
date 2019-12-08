package core.userDefinedTask.internals;

import java.io.File;

public class DefaultTools implements ITools {

	private static final DefaultTools INSTANCE = new DefaultTools();
	private ITools executor = LocalTools.of();

	public synchronized static void setExecutor(ITools executor) {
		INSTANCE.executor = executor;
	}

	public static DefaultTools get() {
		return INSTANCE;
	}

	@Override
	public String getClipboard() {
		return executor.getClipboard();
	}

	@Override
	public boolean setClipboard(String data) {
		return executor.setClipboard(data);
	}

	@Override
	public String execute(String command) {
		return executor.execute(command);
	}

	@Override
	public String execute(String command, String cwd) {
		return execute(command, new File(cwd));
	}

	@Override
	public String execute(String command, File cwd) {
		return executor.execute(command, cwd);
	}

}
