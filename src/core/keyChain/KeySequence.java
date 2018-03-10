package core.keyChain;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import argo.jdom.JsonNode;
import utilities.KeyCodeToChar;

/**
 * Sequence of keys sorted chronologically. There is no guarantee that one is
 * released before another is pressed.
 */
public class KeySequence extends KeySeries {

	private static final Logger LOGGER = Logger.getLogger(KeyChain.class.getName());

	public KeySequence() {
		super();
	}

	public KeySequence(List<KeyStroke> keys) {
		super(keys);
	}

	/**
	 * Check if two {@link KeySequence} collides when applied. Formally, return true
	 * if triggering one {@link KeySequence} forces the other to be triggered. To
	 * trigger a KeyChain is to press all the keys in the given order (there is no
	 * constraint on releasing one key while pressing another).
	 */
	@Override
	public boolean collideWith(KeySeries other) {
		if (getClass() != other.getClass()) {
			throw new IllegalArgumentException("Cannot compare " + getClass() + " with " + other.getClass());
		}

		List<KeyStroke> keys = getKeyStrokes();
		List<KeyStroke> otherKeys = other.getKeyStrokes();
		if (keys.size() > otherKeys.size()) {
			return Collections.indexOfSubList(keys, otherKeys) >= 0;
		} else {
			return Collections.indexOfSubList(otherKeys, keys) >= 0;
		}
	}

	/**
	 * Get the string which would be typed out if all keys in this {@link KeySequence} are pressed in the specified order.
	 * Note that this ignores effects of keys like SHIFT, CAPSLOCK, or NUMSLOCK.
	 */
	public String getTypedString() {
		StringBuilder builder = new StringBuilder();
		for (KeyStroke keyStroke : getKeyStrokes()) {
			String s = KeyCodeToChar.getCharForCode(keyStroke.getKey(), keyStroke.getKeyboardState());
			builder.append(s);
		}

		return builder.toString();
	}

	public static KeySequence parseJSON(List<JsonNode> list) {
		try {
			List<KeyStroke> keys = KeySeries.parseKeyStrokes(list);
			if (keys == null) {
				LOGGER.warning("Failed to parse KeyChain!");
				return null;
			}
			return new KeySequence(keys);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to parse KeyChain", e);
			return null;
		}
	}
}
