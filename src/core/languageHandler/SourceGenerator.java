package core.languageHandler;

import java.util.Arrays;

public abstract class SourceGenerator {

	protected final StringBuffer source;

	public SourceGenerator() {
		source = new StringBuffer();
	}

	public abstract boolean submitTask(long time, String device, String action, int[] param);

	protected final boolean verify(String device, String action, int[] param) {
		for (int i = 0; i < param.length; i++) {
			if (i < 0) {
				return false;
			}
		}

		if (device.equals("mouse")) {
			return Arrays.asList("move", "moveBy", "press", "release", "click").contains(action);
		} else if (device.equals("keyBoard")) {
			return Arrays.asList("type", "press", "release").contains(action);
		} else {
			return false;
		}
	}

	public final void clear() {
		source.setLength(0);
	}

	public abstract String getSource();
}
