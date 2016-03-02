package core.controller;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import utilities.Function;

/**
 * Class to provide API to control mouse
 * @author HP Truong
 *
 */
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
				KeyEvent.VK_SEMICOLON, KeyEvent.VK_QUOTE, KeyEvent.VK_BACK_QUOTE };

		Character[] inputs = new Character[] { '!', '@', '#', '$', '%', '^',
				'&', '*', '(', ')', '_', '+', '<', '>', '?', '|', '{', '}',
				':', '"' , '~'};

		for (int i = 0; i < inputs.length; i++) {
			final int index = i;
			charShiftType.put(inputs[index], new Function<Robot, Void>() {
				@Override
				public Void apply(Robot r) {
					r.keyPress(KeyEvent.VK_SHIFT);
					r.keyPress(keys[index]);
					r.keyRelease(KeyEvent.VK_SHIFT);
					r.keyRelease(keys[index]);
					return null;
				}
			});
		}
	}

	public static final int TYPE_DURATION_MS = 20;
	private final Robot controller;

	protected KeyboardCore(Robot controller) {
		this.controller = controller;
	}

	/**
	 * Simulate keyboard type to type out a string. This types upper case letter by using SHIFT + lower case letter.
	 * Almost every typeable character on ANSI keyboard is supported.
	 * @param string string to be typed.
	 */
	public void type(String string) {
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			type(c);
		}
	}

	/**
	 * Simulate keyboard type to type out an array of string in the respective order as appeared in array.
	 * This types upper case letter by using SHIFT + lower case letter.
	 * Almost every typeable character on ANSI keyboard is supported.
	 * @param strings array of strings to be typed.
	 */
	public void type(String...strings) {
		for (String s : strings) {
			type(s);
		}
	}

	/**
	 * Simulate keyboard type to type out a character. This types upper case letter by using SHIFT + lower case letter.
	 * Almost every typeable character on ANSI keyboard is supported.
	 * @param c character to be typed
	 */
	public void type(char c) {
		if (Character.isAlphabetic(c)) {
			typeAlphabetic(c);
		} else if (charShiftType.containsKey(c)) {
			charShiftType.get(c).apply(controller);
		} else if (!typeSpecialChar(c)) {
			typeUnknown(c);
		}
	}

	/**
	 * Simulate keyboard to type out a special character. There are only several special characters supported.
	 * @param c the special character to be typed out
	 * @return if the character is supported by this method
	 */
	private boolean typeSpecialChar(char c) {
		switch (c) {
		case '\t':
			controller.keyPress(KeyEvent.VK_TAB);
			controller.keyRelease(KeyEvent.VK_TAB);
			return true;
		case '\n':
			controller.keyPress(KeyEvent.VK_ENTER);
			controller.keyRelease(KeyEvent.VK_ENTER);
			return true;
		default:
			return false;
		}
	}

	/**
	 * Type an alphabetic latin character
	 * @param c character to be typed
	 */
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


	/**
	 * Type a character that is neither an alphabetic character and not in list of known characters (see list defined in this class)
	 * @param c character to be typed
	 */
	private void typeUnknown(char c) {
		int converted = KeyEvent.getExtendedKeyCodeForChar(c);
		controller.keyPress(converted);
		controller.keyRelease(converted);
	}

	/**
	 * Type a key on the keyboard. Key integers are as specified in {@link java.awt.event.KeyEvent} class
	 * @param key integer representing the key as specified in java.awt.events.KeyEvent class
	 * @throws InterruptedException
	 */
	public void type(int key) throws InterruptedException {
		hold(key, TYPE_DURATION_MS);
	}

	/**
	 * Type a series of keys on the keyboard
	 * @param keys array of keys representing the keys as specified in {@link java.awt.event.KeyEvent} class
	 * @throws InterruptedException
	 */
	public void type(int...keys) throws InterruptedException {
		for (int key : keys) {
			type(key);
		}
	}

	/**
	 * Type a combination of keys. E.g. control + C, control + alt + delete
	 * @param keys the array of keys that form the combination in the order. Key integers are as specified in {@link java.awt.event.KeyEvent} class
	 */
	public void combination(int...keys) {
		press(keys);

		for (int i = keys.length - 1; i >= 0; i--) {
			release(keys[i]);
		}
	}

	/**
	 * Hold a key for a certain duration
	 * @param key the integer representing the key to be held. See {@link java.awt.event.KeyEvent} class for these integers
	 * @param duration duration to hold key in milliseconds
	 * @throws InterruptedException
	 */
	public void hold(int key, int duration) throws InterruptedException {
		press(key);

		if (duration >= 0) {
			Thread.sleep(duration);
		}

		release(key);
	}

	/**
	 * Press a key
	 * @param key the integer representing the key to be pressed. See {@link java.awt.event.KeyEvent} class for these integers
	 */
	public void press(int key) {
		controller.keyPress(key);
	}

	/**
	 * Press a series of key
	 * @param keys the array of keys to be pressed. See {@link java.awt.event.KeyEvent} class for these integers
	 */
	public void press(int...keys) {
		for (int key : keys) {
			press(key);
		}
	}

	/**
	 * Release a key
	 * @param key key the integer representing the key to be released. See {@link java.awt.event.KeyEvent} class for these integers
	 */
	public void release(int key) {
		controller.keyRelease(key);
	}

	/**
	 * Release a series key
	 * @param keys the array of keys to be released. See {@link java.awt.event.KeyEvent} class for these integers
	 */
	public void release(int...keys) {
		for (int key : keys) {
			release(key);
		}
	}
}
