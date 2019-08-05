package core.controller;

import core.controller.internals.AbstractKeyboardCoreImplementation;

/**
 * Class to provide API to control keyboard.
 * @author HP Truong
 *
 */
public class KeyboardCore extends AbstractKeyboardCoreImplementation {

	private final AbstractKeyboardCoreImplementation k;

	protected KeyboardCore(AbstractKeyboardCoreImplementation k) {
		this.k = k;
	}

	@Override
	public void type(String... strings) {
		k.type(strings);
	}

	@Override
	public void type(char... chars) {
		k.type(chars);
	}

	@Override
	public void type(int... keys) throws InterruptedException {
		k.type(keys);
	}

	@Override
	public void combination(int... keys) {
		k.combination(keys);
	}

	@Override
	public void hold(int key, int duration) throws InterruptedException {
		k.hold(key, duration);
	}

	@Override
	public void press(int... keys) {
		k.press(keys);
	}

	@Override
	public void release(int... keys) {
		k.release(keys);
	}

	/**
	 * Type a key multiple times
	 * @deprecated use {@link #repeat(int, int...)} instead
	 * @param key integer representing the key as specified in java.awt.events.KeyEvent class
	 * @param count number of times to repeat the typing
	 * @throws InterruptedException
	 */
	@Deprecated
	public void typeRepeat(int key, int count) throws InterruptedException {
		k.repeat(key, count);
	}

	@Override
	public boolean isLocked(int key) {
		return k.isLocked(key);
	}
}
