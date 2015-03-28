package core;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import utilities.Function;
import utilities.StringUtilities;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.config.IJsonable;
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
		}.applyList(keys), " + ");
	}

	@Override
	public int hashCode() {
		return keys.hashCode();
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
		}.applyList(keys);
		return JsonNodeFactories.array(hotkeyChain);
	}

	public static KeyChain parseJSON(List<JsonNode> list) {
		try {
			return new KeyChain(new Function<JsonNode, Integer>() {
				@Override
				public Integer apply(JsonNode d) {
					return Integer.parseInt(d.getText());
				}
			}.applyList(list));
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to parse KeyChain", e);
			return null;
		}
	}
}
