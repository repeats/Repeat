package core.userDefinedTask.manualBuild.steps;

import java.awt.event.InputEvent;

class DisplayTextUtil {

	public static String coordinate(int x, int y) {
		return String.format("(%d, %d)", x, y);
	}

	public static String mouseMaskToString(int mask) {
		switch (mask) {
		case InputEvent.BUTTON1_DOWN_MASK:
			return "left button";
		case InputEvent.BUTTON3_DOWN_MASK:
			return "right button";
		case InputEvent.BUTTON2_DOWN_MASK:
			return "middle";
		default:
			return "unknown button";
		}
	}

	private DisplayTextUtil() {}
}
