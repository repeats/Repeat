package core.keyChain;

/**
 * The state of lock keys on the keyboard (numslock, capslock, scrolllock).
 * Instances of this class is immutable.
 */
public class KeyboardState {
	private boolean numslockLocked;
	private boolean capslockLocked;
	private boolean scrollLockLocked;
	private boolean shiftLocked;

	public static KeyboardState getDefault() {
		return new KeyboardState(false, false, false, false);
	}

	public static KeyboardState of(boolean numlockLocked, boolean capslockLocked, boolean scrollLockLocked, boolean isShiftLocked) {
		return new KeyboardState(numlockLocked, capslockLocked, scrollLockLocked, isShiftLocked);
	}

	private KeyboardState(boolean numlockLocked, boolean capslockLocked, boolean scrollLockLocked, boolean isShiftLocked) {
		this.numslockLocked = numlockLocked;
		this.capslockLocked = capslockLocked;
		this.scrollLockLocked = scrollLockLocked;
		this.shiftLocked = isShiftLocked;
	}

	public KeyboardState withNumslockOn() {
		return clone().setNumslockLocked(true);
	}

	public KeyboardState withNumslockOff() {
		return clone().setNumslockLocked(false);
	}

	public KeyboardState withCapslockOn() {
		return clone().setCapslockLocked(true);
	}

	public KeyboardState withCapslockOff() {
		return clone().setCapslockLocked(false);
	}

	public KeyboardState withScrollLockOn() {
		return clone().setScrollLockLocked(true);
	}

	public KeyboardState withScrollLockOff() {
		return clone().setScrollLockLocked(false);
	}

	public KeyboardState withShiftLocked() {
		return clone().setShiftLocked(true);
	}

	public KeyboardState withShiftUnlocked() {
		return clone().setShiftLocked(false);
	}

	@Override
	public KeyboardState clone() {
		return of(numslockLocked, capslockLocked, scrollLockLocked, shiftLocked);
	}

	public boolean isNumslockLocked() {
		return numslockLocked;
	}
	public boolean isCapslockLocked() {
		return capslockLocked;
	}
	public boolean isScrollLockLocked() {
		return scrollLockLocked;
	}
	public boolean isShiftLocked() {
		return shiftLocked;
	}

	private KeyboardState setNumslockLocked(boolean numslockLocked) {
		this.numslockLocked = numslockLocked;
		return this;
	}
	private KeyboardState setCapslockLocked(boolean capslockLocked) {
		this.capslockLocked = capslockLocked;
		return this;
	}
	private KeyboardState setScrollLockLocked(boolean scrollLockLocked) {
		this.scrollLockLocked = scrollLockLocked;
		return this;
	}
	private KeyboardState setShiftLocked(boolean shiftLocked) {
		this.shiftLocked = shiftLocked;
		return this;
	}
}
