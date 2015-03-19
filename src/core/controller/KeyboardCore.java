package core.controller;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import utilities.Function;

public class KeyboardCore {

	private static final HashMap<Character, Function<Robot, Void>> charShiftType;

	static {
		charShiftType = new HashMap<>();

		final int[] keys = new int[] { KeyEvent.VK_1, KeyEvent.VK_2,
				KeyEvent.VK_3, KeyEvent.VK_4, KeyEvent.VK_5, KeyEvent.VK_6,
				KeyEvent.VK_7, KeyEvent.VK_8, KeyEvent.VK_9, KeyEvent.VK_0,
				KeyEvent.VK_MINUS, KeyEvent.VK_EQUALS, KeyEvent.VK_COMMA,
				KeyEvent.VK_PERIOD, KeyEvent.VK_SLASH, KeyEvent.VK_BACK_SLASH,
				KeyEvent.VK_OPEN_BRACKET, KeyEvent.VK_CLOSE_BRACKET,
				KeyEvent.VK_SEMICOLON, KeyEvent.VK_QUOTE };

		Character[] inputs = new Character[] { '!', '@', '#', '$', '%', '^',
				'&', '*', '(', ')', '_', '+', '<', '>', '?', '|', '{', '}',
				':', '"' };

		for (int i = 0; i < inputs.length; i++) {
			final int index = i;
			charShiftType.put(inputs[index], new Function<Robot, Void>() {
				@Override
				public Void apply(Robot r) {
					r.keyPress(KeyEvent.VK_SHIFT);
					r.keyPress(keys[index]);
					r.keyRelease(KeyEvent.VK_SHIFT);
					return null;
				}
			});
		}
	}

	public static final int TYPE_DURATION_MS = 20;
	private Robot controller;

	public KeyboardCore(Robot controller) {
		this.controller = controller;
	}

	public void type(String string) {
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			type(c);
		}
	}

	public void type(char c) {
		if (Character.isAlphabetic(c)) {
			typeAlphabetic(c);
		} else if (charShiftType.containsKey(c)) {
			charShiftType.get(c).apply(controller);
		} else {
			typeUnknown(c);
		}
	}

	private void typeAlphabetic(char c) {
		if (Character.isUpperCase(c)) {
			controller.keyPress(KeyEvent.VK_SHIFT);
		}
		controller.keyPress(Character.toUpperCase(c));
		controller.keyRelease(Character.toUpperCase(c));

		if (Character.isUpperCase(c)) {
			controller.keyRelease(KeyEvent.VK_SHIFT);
		}
	}

	private void typeUnknown(char c) {
		controller.keyPress(KeyEvent.getExtendedKeyCodeForChar(c));
	}

	public void type(int key) {
		hold(key, TYPE_DURATION_MS);
	}

	public void hold(int key, int duration) {
		press(key);

		if (duration >= 0) {
			controller.delay(duration);
			release(key);
		}
	}

	public void press(int key) {
		controller.keyPress(key);
	}

	public void release(int key) {
		controller.keyRelease(key);
	}
}
