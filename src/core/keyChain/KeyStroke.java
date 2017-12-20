package core.keyChain;

/**
 * Represents a key stroke on the keyboard.
 */
public class KeyStroke {
	private int key;
	private int modifier;

	public static KeyStroke Of(int key, int modifier) {
		return new KeyStroke(key, modifier);
	}

	private KeyStroke(int key, int modifier) {
		this.key = key;
		this.modifier = modifier;
	}

	public int getKey() {
		return key;
	}

	public int getModifier() {
		return modifier;
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
