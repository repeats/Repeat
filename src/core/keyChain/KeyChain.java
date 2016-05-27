package core.keyChain;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import utilities.Function;
import utilities.IJsonable;
import utilities.StringUtilities;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.config.Parser1_0;

public class KeyChain implements IJsonable {

	private static final Logger LOGGER = Logger.getLogger(Parser1_0.class.getName());
	private final List<Integer> keys;

	public KeyChain(List<Integer> keys) {
		this.keys = keys;
	}

	public KeyChain(int key) {
		this(Arrays.asList(key));
	}

	public KeyChain() {
		this(new ArrayList<Integer>());
	}

	/**
	 * Check if two KeyChain will collide when applied. Formally,
	 * return true if triggering one KeyChain forces the other to be triggered. To trigger a KeyChain
	 * is to press all the keys in this.keys in the given order, without releasing any key in the process.
	 *
	 * For example,
	 * A + S + D collides with A + S, but not with S + D or D + S
	 * Ctrl + Shift + C does not collide with Ctrl + C
	 *
	 * @param other other KeyChain to check for collision.
	 * @return true if this key chain collides with the other key chain
	 */
	public boolean collideWith(KeyChain other) {
		if (keys.size() > other.keys.size()) {
			return Collections.indexOfSubList(keys, other.keys) == 0;
		} else {
			return Collections.indexOfSubList(other.keys, keys) == 0;
		}
	}

	public List<Integer> getKeys() {
		return keys;
	}

	@Override
	public String toString() {
		return StringUtilities.join(new Function<Integer, String>() {
			@Override
			public String apply(Integer r) {
				return KeyEvent.getKeyText(r);
			}
		}.map(keys), " + ");
	}

	@Override
	public KeyChain clone() {
		return new KeyChain(new ArrayList<>(this.keys));
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
		KeyChain other = (KeyChain) obj;
		if (keys == null) {
			if (other.keys != null) {
				return false;
			}
		} else {
			if (this.keys.size() != other.keys.size()) {
				return false;
			}
			for (int i = 0; i < this.keys.size(); i++) {
				if (this.keys.get(i) != other.keys.get(i)) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public JsonRootNode jsonize() {
		List<Integer> keys = this.keys == null ? new ArrayList<Integer>() : this.keys;

		List<JsonNode> hotkeyChain = new Function<Integer, JsonNode>() {
			@Override
			public JsonNode apply(Integer r) {
				return JsonNodeFactories.number(r);
			}
		}.map(keys);

		return JsonNodeFactories.array(hotkeyChain);
	}

	public static KeyChain parseJSON(List<JsonNode> list) {
		try {
			return new KeyChain(new Function<JsonNode, Integer>() {
				@Override
				public Integer apply(JsonNode d) {
					return Integer.parseInt(d.getText());
				}
			}.map(list));
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to parse KeyChain", e);
			return null;
		}
	}
}
