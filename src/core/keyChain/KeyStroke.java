package core.keyChain;

/**
 * Represents a key stroke on the keyboard.
 */
public class KeyStroke {
	private int key;
	private int modifier;
	public static final int KEY_MODIFIER_RIGHT = 2;
	public static final int KEY_MODIFIER_LEFT = 1;
	public static final int KEY_MODIFIER_UNKNOWN = 0;

	public static KeyStroke Of(int key, int modifier) {
		return new KeyStroke(key, modifier);
	}

	private KeyStroke(int key, int modifier) {
		this.key = key;
		this.modifier = modifier;
	}

	/**
	 * Retrieve the key on the keyboard. This alone does not identify the exact key
	 * for cases like Ctrl and Shift, which have left and right keys.
	 *
	 * @return the integer representing the key on the keyboard.
	 */
	public int getKey() {
		return key;
	}

	/**
	 * Syntactic sugar for {@link #getKey()}.
	 */
	public int k() {
		return getKey();
	}

	/**
	 * Retrieve the modifier of the key stroke. Either left or right for
	 * keys like shift, ctrl or alt, or has undefined meaning for keys
	 * that have only 1 key on the keyboard (virtually all others).
	 *
	 * @return the modifier of the key stroke.
	 */
	public int getModifier() {
		return modifier;
	}

	/**
	 * Syntactic sugar for {@link #getModifier()}.
	 */
	public int m() {
		return getModifier();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + key;
		result = prime * result + modifier;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		KeyStroke other = (KeyStroke) obj;
		if (key != other.key) {
			return false;
		}
		if (modifier != other.modifier) {
			return false;
		}
		return true;
	}
}
