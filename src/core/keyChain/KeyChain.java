package core.keyChain;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import argo.jdom.JsonNode;

/**
 * Represents keys pressed in a "hotkey" manner like
 * Ctrl + C, Ctrl + V, Ctrl + Alt + Del.
 */
public class KeyChain extends KeySeries {

	private static final Logger LOGGER = Logger.getLogger(KeyChain.class.getName());

	public KeyChain(List<ButtonStroke> keys) {
		super(keys);
	}

	public KeyChain(Iterable<Integer> keys) {
		super(keys);
	}

	public KeyChain(int key) {
		super(key);
	}

	public KeyChain() {
		super();
	}

	@Override
	public KeyChain clone() {
		return new KeyChain(keys);
	}

	/**
	 * Check if two {@link KeyChain} will collide when applied. Formally, return
	 * true if triggering one KeyChain forces the other to be triggered. To trigger
	 * a {@link KeyChain} is to press all the keys in this.keys in the given order,
	 * without releasing any key in the process.
	 *
	 * For example, A + S + D collides with A + S, but not with S + D or D + S Ctrl
	 * + Shift + C does not collide with Ctrl + C
	 *
	 * @param other
	 *            other KeyChain to check for collision.
	 * @return true if this key chain collides with the other key chain
	 */
	@Override
	public boolean collideWith(KeySeries other) {
		if (getClass() != other.getClass()) {
			throw new IllegalArgumentException("Cannot compare " + getClass() + " with " + other.getClass());
		}

		List<ButtonStroke> keys = getButtonStrokes();
		List<ButtonStroke> otherKeys = other.getButtonStrokes();
		if (keys.size() > otherKeys.size()) {
			return Collections.indexOfSubList(keys, otherKeys) == 0;
		} else {
			return Collections.indexOfSubList(otherKeys, keys) == 0;
		}
	}

	public static KeyChain parseJSON(List<JsonNode> list) {
		try {
			List<ButtonStroke> keys = KeySeries.parseKeyStrokes(list);
			if (keys == null) {
				LOGGER.warning("Failed to parse KeyChain!");
				return null;
			}
			return new KeyChain(keys);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to parse KeyChain", e);
			return null;
		}
	}
}
