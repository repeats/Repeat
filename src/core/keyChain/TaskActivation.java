package core.keyChain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import utilities.IJsonable;
import utilities.JSONUtility;


public class TaskActivation implements IJsonable {

	private static final Logger LOGGER = Logger.getLogger(TaskActivation.class.getName());

	private Set<KeyChain> hotkeys;
	private Set<MouseGesture> mouseGestures;

	private TaskActivation(Builder builder) {
		hotkeys = builder.hotkeys;
		mouseGestures = builder.mouseGestures;
	}

	/**
	 * @param hotkeys the hotkey set to set
	 */
	public final void setHotKeys(Set<KeyChain> hotkeys) {
		this.hotkeys = hotkeys;
	}

	/**
	 * @return the set of key chains associated with this activation entity.
	 */
	public final Set<KeyChain> getHotkeys() {
		if (hotkeys == null) {
			hotkeys = new HashSet<KeyChain>();
		}
		return hotkeys;
	}

	/**
	 * @return an arbitrary {@link KeyChain} from the set of keychains, or null if the set is empty.
	 */
	public final KeyChain getFirstHotkey() {
		Set<KeyChain> hotkeys = getHotkeys();
		if (hotkeys.isEmpty()) {
			return null;
		} else {
			return hotkeys.iterator().next();
		}
	}

	/**
	 * @param mouseGestures set of mouse gestures to set.
	 */
	public final void setMouseGestures(Set<MouseGesture> mouseGestures) {
		this.mouseGestures = mouseGestures;
	}

	/**
	 * @return set of mouse gestures associated with this activation entity.
	 */
	public final Set<MouseGesture> getMouseGestures() {
		if (mouseGestures == null) {
			mouseGestures = new HashSet<>();
		}

		return mouseGestures;
	}

	/**
	 * @return an arbitrary {@link MouseGesture} from the set of gestures, or null if the set is empty.
	 */
	public final MouseGesture getFirstMouseGesture() {
		Set<MouseGesture> gestures = getMouseGestures();
		if (gestures.isEmpty()) {
			return null;
		} else {
			return gestures.iterator().next();
		}
	}

	/**
	 * Copy the content of the other {@link TaskActivation} to this object.
	 *
	 * @param other other task activation whose content will be copied from.
	 */
	public final void copy(TaskActivation other) {
		setHotKeys(other.getHotkeys());
		setMouseGestures(other.getMouseGestures());
	}

	/**
	 * Check if this activation is empty (i.e. no event for activation).
	 */
	public final boolean isEmpty() {
		return getHotkeys().isEmpty() && getMouseGestures().isEmpty();
	}

	@Override
	public JsonRootNode jsonize() {
		return JsonNodeFactories.object(
				JsonNodeFactories.field("hotkey", JsonNodeFactories.array(JSONUtility.listToJson(getHotkeys()))),
				JsonNodeFactories.field("mouse_gesture", JsonNodeFactories.array(JSONUtility.listToJson(getMouseGestures()))));
	}

	/**
	 * Construct a new object from a json node.
	 *
	 * @param node json node to parse
	 * @return the new object with content parsed, or null if cannot parse.
	 */
	public static TaskActivation parseJSON(JsonNode node) {
		try {
			List<JsonNode> hotkeysNode = node.getArrayNode("hotkey");
			List<JsonNode> mouseGestureNode = node.getArrayNode("mouse_gesture");

			Set<KeyChain> keyChains = new HashSet<>();
			for (JsonNode hotkeyNode : hotkeysNode) {
				KeyChain newKeyChain = KeyChain.parseJSON(hotkeyNode.getArrayNode());
				if (newKeyChain == null) {
					LOGGER.log(Level.WARNING, "Cannot parse key chain " + hotkeyNode);
				} else {
					keyChains.add(newKeyChain);
				}
			}

			Set<MouseGesture> gestures = MouseGesture.parseJSON(mouseGestureNode);
			TaskActivation output = TaskActivation.newBuilder().withHotKeys(keyChains).withMouseGestures(gestures).build();
			return output;
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Exception while parsing task activation.", e);
			return null;
		}
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	/**
	 * Convenient constructor to build a one key chain one hot key activation.
	 *
	 * @param keys
	 *            list of keys in the key chain.
	 * @return the built task activation.
	 */
	public static TaskActivation combination(int... keys) {
		List<Integer> ks = new ArrayList<>(keys.length);
		for (int key : keys) {
			ks.add(key);
		}
		return newBuilder().addHotKeys(new KeyChain(ks)).build();
	}

	/**
	 * Convenient constructor to build a one mouse gesture activation.
	 *
	 * @param gesture
	 *            mouse gesture.
	 * @return the built task activation.
	 */
	public static TaskActivation gesture(MouseGesture gesture) {
		return newBuilder().addMouseGesture(gesture).build();
	}

	/**
	 * Builder for enclosing class.
	 */
	public static class Builder {

		private Set<KeyChain> hotkeys;
		private Set<MouseGesture> mouseGestures;

		private Builder() {
			hotkeys = new HashSet<>();
			mouseGestures = new HashSet<>();
		}

		public Builder addHotKeys(KeyChain... keys) {
			hotkeys.addAll(Arrays.asList(keys));
			return this;
		}

		public Builder withHotKey(KeyChain key) {
			hotkeys = new HashSet<>();
			hotkeys.add(key);
			return this;
		}

		public Builder withHotKeys(Set<KeyChain> keys) {
			this.hotkeys = keys;
			return this;
		}

		public Builder addMouseGesture(MouseGesture... gestures) {
			mouseGestures.addAll(Arrays.asList(gestures));
			return this;
		}

		public Builder withMouseGesture(MouseGesture gesture) {
			mouseGestures = new HashSet<>();
			mouseGestures.add(gesture);
			return this;
		}

		public Builder withMouseGestures(Set<MouseGesture> gestures) {
			this.mouseGestures = gestures;
			return this;
		}

		public TaskActivation build() {
			return new TaskActivation(this);
		}
	}
}
