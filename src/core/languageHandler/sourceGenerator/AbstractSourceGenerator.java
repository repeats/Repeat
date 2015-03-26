package core.languageHandler.sourceGenerator;

import java.util.Arrays;

public abstract class AbstractSourceGenerator {

	protected final StringBuffer source;
	protected TaskSourceScheduler sourceScheduler;

	protected static final String TAB = "    ";
	protected static final String TWO_TAB = TAB + TAB;
	protected static final String THREE_TAB = TWO_TAB + TAB;
	protected static final String FOUR_TAB = THREE_TAB + TAB;

	public AbstractSourceGenerator() {
		source = new StringBuffer();
		sourceScheduler = new TaskSourceScheduler();
	}

	public final boolean submitTask(long time, String device, String action, int[] param) {
		if (!verify(device, action, param)) {
			return false;
		}

		return internalSubmitTask(time, device, action, param);
	}

	protected abstract boolean internalSubmitTask(long time, String device, String action, int[] param);

	protected final boolean verify(String device, String action, int[] param) {
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
