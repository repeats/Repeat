package core.keyChain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import utilities.Function;
import utilities.IJsonable;
import utilities.KeyCodeToChar;
import utilities.StringUtilities;

public abstract class KeySeries implements IJsonable {

	private static final Logger LOGGER = Logger.getLogger(KeySeries.class.getName());
	protected List<KeyStroke> keys;

	public KeySeries(List<KeyStroke> keys) {
		this.keys = new ArrayList<>(keys.size());

		for (KeyStroke key : keys) {
			this.keys.add(key);
		}
	}

	public KeySeries(Iterable<Integer> keys) {
		this.keys = new ArrayList<>();

		for (Integer key : keys) {
			this.keys.add(KeyStroke.of(key, KeyStroke.Modifier.KEY_MODIFIER_UNKNOWN));
		}
	}

	public KeySeries(int key) {
		this(Arrays.asList(key));
	}

	public KeySeries() {
		this(new ArrayList<Integer>());
	}

	public abstract boolean collideWith(KeySeries other);

	/**
	 * @return list of key codes in this key chain.
	 * @deprecated change to use {@link #getKeyStrokes()} instead.
	 */
	@Deprecated
	public List<Integer> getKeys() {
		List<Integer> output = new ArrayList<>();
		for (KeyStroke key : keys) {
			output.add(key.getKey());
		}
		return output;
	}

	/**
	 * @return the list of key strokes contained in this key chain.
	 */
	public List<KeyStroke> getKeyStrokes() {
		List<KeyStroke> output = new ArrayList<>(keys.size());
		for (KeyStroke key : keys) {
			output.add(key.clone());
		}
		return output;
	}

	/**
	 * @return the number of key strokes in this key chain.
	 */
	public int getSize() {
		return keys.size();
	}

	/*
	 * Add all key strokes from another key chain.
	 */
	public void addFrom(KeySeries other) {
		keys.addAll(other.keys);
	}

	/**
	 * Add a single stroke to the key chain.
	 * @param stroke stroke to add.
	 */
	public void addKeyStroke(KeyStroke stroke) {
		keys.add(stroke);
	}

	/**
	 * Remove all keys in this key chain.
	 */
	public void clearKeys() {
		keys.clear();
	}

	/**
	 * Check whether this key chain contains no key.
	 * @return if there is no key stroke in this key chain.
	 */
	public boolean isEmpty() {
		return keys.isEmpty();
	}

	/**
	 * @param stroke the key stroke to find.
	 * @return whether the given key stroke is in this key chain.
	 */
	public boolean contains(KeyStroke stroke) {
		for (KeyStroke key : keys) {
			if (key.equals(stroke)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the string which would be typed out if all keys in this {@link KeySequence} are pressed in the specified order.
	 * Note that this ignores effects of keys like SHIFT, CAPSLOCK, or NUMSLOCK.
	 */
	public String getTypedString() {
		StringBuilder builder = new StringBuilder();
		KeyboardState keyboardState = KeyboardState.getDefault();

		for (KeyStroke keyStroke : getKeyStrokes()) {
			String s = KeyCodeToChar.getCharForCode(keyStroke.getKey(), keyboardState);
			builder.append(s);
		}

		return builder.toString();
	}

	@Override
	public String toString() {
		return StringUtilities.join(new Function<KeyStroke, String>() {
			@Override
			public String apply(KeyStroke k) {
				return k.toString();
			}
		}.map(keys), " + ");
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
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
		KeySeries other = (KeySeries) obj;
		if (keys == null) {
			if (other.keys != null) {
				return false;
			}
		} else {
			return this.keys.equals(other.keys);
		}
		return true;
	}

	@Override
	public JsonRootNode jsonize() {
		List<JsonNode> keyChain = new Function<KeyStroke, JsonNode>() {
			@Override
			public JsonNode apply(KeyStroke s) {
				return s.jsonize();
			}
		}.map(getKeyStrokes());

		return JsonNodeFactories.array(keyChain);
	}

	public static List<KeyStroke> parseKeyStrokes(List<JsonNode> list) {
		try {
			List<KeyStroke> keys = new Function<JsonNode, KeyStroke>() {
				@Override
				public KeyStroke apply(JsonNode n) {
					return KeyStroke.parseJSON(n);
				}
			}.map(list);
			return keys;
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to parse key series", e);
			return null;
		}
	}
}
