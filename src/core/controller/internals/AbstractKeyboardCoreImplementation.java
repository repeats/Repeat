package core.controller.internals;

public abstract class AbstractKeyboardCoreImplementation {

	/**
	 * Simulate keyboard type to type out a string. This types upper case letter by using SHIFT + lower case letter.
	 * Almost every typeable character on ANSI keyboard is supported.
	 *
	 * If config use clipboard is enabled, this puts the string into clipboard and uses
	 * SHIFT + INSERT to paste the content instead of typing out.
	 * Note that this will preserve the text clipboard.
	 *
	 * @param string string to be typed.
	 */
	public final void type(String string) {
		type(new String[] { string });
	}

	/**
	 * Type out a series of strings using {@link #type(String)}.
	 * @param strings array of strings to be typed.
	 */
	public abstract void type(String...strings);

	/**
	 * Simulate keyboard type to type out a character. This types upper case letter by using SHIFT + lower case letter.
	 * Almost every typeable character on ANSI keyboard is supported.
	 * @param c character to be typed
	 */
	public final void type(char c) {
		type(new char[] {c});
	}

	/**
	 * Simulate keyboard type to type out a series of characters. This types upper case letter by using SHIFT + lower case letter.
	 * Almost every typeable character on ANSI keyboard is supported.
	 * @param chars characters to be typed
	 */
	public abstract void type(char... chars);

	/**
	 * Type a key on the keyboard. Key integers are as specified in {@link java.awt.event.KeyEvent} class
	 * @param key integer representing the key as specified in java.awt.events.KeyEvent class
	 * @throws InterruptedException
	 */
	public final void type(int key) throws InterruptedException {
		type(new int[] { key });
	}

	/**
	 * Type a series of keys on the keyboard
	 * @param keys array of keys representing the keys as specified in {@link java.awt.event.KeyEvent} class
	 * @throws InterruptedException
	 */
	public abstract void type(int...keys) throws InterruptedException;

	/**
	 * Type a sequence of keys sequentially multiple times
	 * @param count number of times to repeat the typing
	 * @param key integers representing the keys as specified in java.awt.events.KeyEvent class
	 * @throws InterruptedException
	 */
	public final void repeat(int count, int...keys) throws InterruptedException {
		if (count <= 0) {
			return;
		}

		for (int i = 0; i < count; i++) {
			type(keys);
		}
	}

	/**
	 * Type a combination of keys. E.g. control + C, control + alt + delete
	 * @param keys the array of keys that form the combination in the order. Key integers are as specified in {@link java.awt.event.KeyEvent} class
	 */
	public abstract void combination(int...keys);

	/**
	 * Hold a key for a certain duration
	 * @param key the integer representing the key to be held. See {@link java.awt.event.KeyEvent} class for these integers
	 * @param duration duration to hold key in milliseconds
	 * @throws InterruptedException
	 */
	public abstract void hold(int key, int duration) throws InterruptedException;

	/**
	 * Press a key. The keys are held down after the method finishes.
	 * @param key the integer representing the key to be pressed. See {@link java.awt.event.KeyEvent} class for these integers
	 */
	public final void press(int key) {
		press(new int[] { key });
	}

	/**
	 * Press a series of key. The key is held down after the method finishes.
	 * @param keys the array of keys to be pressed. See {@link java.awt.event.KeyEvent} class for these integers
	 */
	public abstract void press(int...keys);

	/**
	 * Release a key
	 * @param key key the integer representing the key to be released. See {@link java.awt.event.KeyEvent} class for these integers
	 */
	public final void release(int key) {
		release(new int[] { key});
	}

	/**
	 * Release a series of key
	 * @param keys the array of keys to be released. See {@link java.awt.event.KeyEvent} class for these integers
	 */
	public abstract void release(int...keys);

	/**
	 * Check if a key is on (in locked state).
	 * @param key key to check if on E.g. VK_CAPS_LOCk, VK_NUM_LOCK
	 * @return if key locking state is on
	 */
	public abstract boolean isLocked(int key);
}
