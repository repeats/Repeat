package utilities;

import java.awt.event.KeyEvent;

public class KeyEventCodeToString {

	private KeyEventCodeToString() {}

	/**
	 * Convert from code from KeyEvent.VK_* to a human friendly string.
	 * This is needed because in some OSes the KeyEvent utility does
	 * not work properly.
	 */
	public static String codeToString(int code) {
		switch (code) {
		case KeyEvent.VK_CONTROL:
			return "Ctrl";
		case KeyEvent.VK_ALT:
			if (OSIdentifier.IS_OSX) {
				return "Option";
			}
			return "Alt";
		case KeyEvent.VK_WINDOWS:
			return "Windows";
		case KeyEvent.VK_META:
			if (OSIdentifier.IS_LINUX) {
				return "Meta";
			}
			if (OSIdentifier.IS_OSX) {
				return "Command";
			}
			break;
		case KeyEvent.VK_SHIFT:
			return "Shift";
		case KeyEvent.VK_TAB:
			return "Tab";
		}
		return KeyEvent.getKeyText(code);
	}
}
