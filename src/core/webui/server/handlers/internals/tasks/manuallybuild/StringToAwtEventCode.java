package core.webui.server.handlers.internals.tasks.manuallybuild;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StringToAwtEventCode {

	public static final int UNKNOWN_VALUE = -1;

	private static final Map<String, Integer> SUPPORTED_MASKS;
	static {
		SUPPORTED_MASKS = new HashMap<>();
		SUPPORTED_MASKS.put("LEFT", InputEvent.BUTTON1_DOWN_MASK);
		SUPPORTED_MASKS.put("RIGHT", InputEvent.BUTTON3_DOWN_MASK);
		SUPPORTED_MASKS.put("MIDDLE", InputEvent.BUTTON2_DOWN_MASK);
	}

	public static List<String> allSupportedMasks() {
		return SUPPORTED_MASKS.keySet().stream().sorted().collect(Collectors.toList());
	}

	public static boolean isValidMouseMask(String value) {
		return SUPPORTED_MASKS.containsKey(value.toUpperCase());
	}

	/**
	 * @return {@link #UNKNOWN_VALUE} if there is no such mask.
	 */
	public static int mouseMaskFromString(String value) {
		value = value.toUpperCase();
		if (!isValidMouseMask(value)) {
			return UNKNOWN_VALUE;
		}
		return SUPPORTED_MASKS.get(value);
	}

	private static final Map<String, Integer> SUPPORTED_KEYS;
	static {
		SUPPORTED_KEYS = new HashMap<>();
		SUPPORTED_KEYS.put("A", KeyEvent.VK_A);
		SUPPORTED_KEYS.put("B", KeyEvent.VK_B);
		SUPPORTED_KEYS.put("C", KeyEvent.VK_C);
		SUPPORTED_KEYS.put("D", KeyEvent.VK_D);
		SUPPORTED_KEYS.put("E", KeyEvent.VK_E);
		SUPPORTED_KEYS.put("F", KeyEvent.VK_F);
		SUPPORTED_KEYS.put("G", KeyEvent.VK_G);
		SUPPORTED_KEYS.put("H", KeyEvent.VK_H);
		SUPPORTED_KEYS.put("I", KeyEvent.VK_I);
		SUPPORTED_KEYS.put("J", KeyEvent.VK_J);
		SUPPORTED_KEYS.put("K", KeyEvent.VK_K);
		SUPPORTED_KEYS.put("L", KeyEvent.VK_L);
		SUPPORTED_KEYS.put("M", KeyEvent.VK_M);
		SUPPORTED_KEYS.put("N", KeyEvent.VK_N);
		SUPPORTED_KEYS.put("O", KeyEvent.VK_O);
		SUPPORTED_KEYS.put("P", KeyEvent.VK_P);
		SUPPORTED_KEYS.put("Q", KeyEvent.VK_Q);
		SUPPORTED_KEYS.put("R", KeyEvent.VK_R);
		SUPPORTED_KEYS.put("S", KeyEvent.VK_S);
		SUPPORTED_KEYS.put("T", KeyEvent.VK_T);
		SUPPORTED_KEYS.put("U", KeyEvent.VK_U);
		SUPPORTED_KEYS.put("V", KeyEvent.VK_V);
		SUPPORTED_KEYS.put("W", KeyEvent.VK_W);
		SUPPORTED_KEYS.put("X", KeyEvent.VK_X);
		SUPPORTED_KEYS.put("Y", KeyEvent.VK_Y);
		SUPPORTED_KEYS.put("Z", KeyEvent.VK_Z);
		SUPPORTED_KEYS.put("0", KeyEvent.VK_0);
		SUPPORTED_KEYS.put("1", KeyEvent.VK_1);
		SUPPORTED_KEYS.put("2", KeyEvent.VK_2);
		SUPPORTED_KEYS.put("3", KeyEvent.VK_3);
		SUPPORTED_KEYS.put("4", KeyEvent.VK_4);
		SUPPORTED_KEYS.put("5", KeyEvent.VK_5);
		SUPPORTED_KEYS.put("6", KeyEvent.VK_6);
		SUPPORTED_KEYS.put("8", KeyEvent.VK_8);
		SUPPORTED_KEYS.put("9", KeyEvent.VK_9);
		SUPPORTED_KEYS.put("ESCAPE", KeyEvent.VK_ESCAPE);
		SUPPORTED_KEYS.put("F1", KeyEvent.VK_F1);
		SUPPORTED_KEYS.put("F2", KeyEvent.VK_F2);
		SUPPORTED_KEYS.put("F3", KeyEvent.VK_F3);
		SUPPORTED_KEYS.put("F4", KeyEvent.VK_F4);
		SUPPORTED_KEYS.put("F5", KeyEvent.VK_F5);
		SUPPORTED_KEYS.put("F6", KeyEvent.VK_F6);
		SUPPORTED_KEYS.put("F7", KeyEvent.VK_F7);
		SUPPORTED_KEYS.put("F8", KeyEvent.VK_F8);
		SUPPORTED_KEYS.put("F9", KeyEvent.VK_F9);
		SUPPORTED_KEYS.put("F10", KeyEvent.VK_F10);
		SUPPORTED_KEYS.put("F11", KeyEvent.VK_F11);
		SUPPORTED_KEYS.put("F12", KeyEvent.VK_F12);
		SUPPORTED_KEYS.put("CAPS_LOCK", KeyEvent.VK_CAPS_LOCK);
		SUPPORTED_KEYS.put("NUM_LOCK", KeyEvent.VK_NUM_LOCK);
		SUPPORTED_KEYS.put("SCROLL_LOCK", KeyEvent.VK_SCROLL_LOCK);
		SUPPORTED_KEYS.put("CONTROL", KeyEvent.VK_CONTROL);
		SUPPORTED_KEYS.put("ALT", KeyEvent.VK_ALT);
		SUPPORTED_KEYS.put("COMMAND", KeyEvent.VK_META);
		SUPPORTED_KEYS.put("TAB", KeyEvent.VK_TAB);
		SUPPORTED_KEYS.put("SHIFT", KeyEvent.VK_SHIFT);
		SUPPORTED_KEYS.put("DELETE", KeyEvent.VK_DELETE);
		SUPPORTED_KEYS.put("BACK_SPACE", KeyEvent.VK_BACK_SPACE);
		SUPPORTED_KEYS.put("MINUS", KeyEvent.VK_MINUS);
		SUPPORTED_KEYS.put("SUBTRACT", KeyEvent.VK_MINUS);
		SUPPORTED_KEYS.put("EQUALS", KeyEvent.VK_EQUALS);
		SUPPORTED_KEYS.put("BACK_SLASH", KeyEvent.VK_BACK_SLASH);
		SUPPORTED_KEYS.put("SLASH", KeyEvent.VK_SLASH);
		SUPPORTED_KEYS.put("QUOTE", KeyEvent.VK_QUOTE);
		SUPPORTED_KEYS.put("PERIOD", KeyEvent.VK_PERIOD);
		SUPPORTED_KEYS.put("COMMA", KeyEvent.VK_COMMA);
		SUPPORTED_KEYS.put("SEMICOLON", KeyEvent.VK_SEMICOLON);
		SUPPORTED_KEYS.put("OPEN_BRACKET", KeyEvent.VK_OPEN_BRACKET);
		SUPPORTED_KEYS.put("CLOSE_BRACKET", KeyEvent.VK_CLOSE_BRACKET);
		SUPPORTED_KEYS.put("WINDOWS", KeyEvent.VK_WINDOWS);
		SUPPORTED_KEYS.put("META", KeyEvent.VK_META);
		SUPPORTED_KEYS.put("SPACE", KeyEvent.VK_SPACE);
		SUPPORTED_KEYS.put("BACK_QUOTE", KeyEvent.VK_DEAD_TILDE);
		SUPPORTED_KEYS.put("TILDE", KeyEvent.VK_DEAD_TILDE);
		SUPPORTED_KEYS.put("CONTEXT_MENU", KeyEvent.VK_CONTEXT_MENU);
		SUPPORTED_KEYS.put("ENTER", KeyEvent.VK_ENTER);
		SUPPORTED_KEYS.put("HOME", KeyEvent.VK_HOME);
		SUPPORTED_KEYS.put("END", KeyEvent.VK_END);
		SUPPORTED_KEYS.put("PAGE_UP", KeyEvent.VK_PAGE_UP);
		SUPPORTED_KEYS.put("PAGE_DOWN", KeyEvent.VK_PAGE_DOWN);
		SUPPORTED_KEYS.put("UP", KeyEvent.VK_UP);
		SUPPORTED_KEYS.put("DOWN", KeyEvent.VK_DOWN);
		SUPPORTED_KEYS.put("LEFT", KeyEvent.VK_LEFT);
		SUPPORTED_KEYS.put("RIGHT", KeyEvent.VK_RIGHT);
	}

	public static List<String> allSupportedKeys() {
		return SUPPORTED_KEYS.keySet().stream().sorted().collect(Collectors.toList());
	}

	public static boolean isValidKeyValue(String value) {
		return SUPPORTED_KEYS.containsKey(value.toUpperCase());
	}

	/**
	 * @return {@link #UNKNOWN_VALUE} if there is no such key code.
	 */
	public static int keyCodeFromString(String value) {
		value = value.toUpperCase();
		if (!isValidKeyValue(value)) {
			return UNKNOWN_VALUE;
		}
		return SUPPORTED_KEYS.get(value);
	}

	private StringToAwtEventCode() {}
}
