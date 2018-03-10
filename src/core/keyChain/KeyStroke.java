package core.keyChain;

import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.util.logging.Logger;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import utilities.IJsonable;

/**
 * Represents a key stroke on the keyboard.
 */
public class KeyStroke implements IJsonable {

	private static final Logger LOGGER = Logger.getLogger(KeyStroke.class.getName());

	public static enum Modifier {
		KEY_MODIFIER_UNKNOWN(0), // Unknown is equal to both left and right.
		KEY_MODIFIER_LEFT(1),
		KEY_MODIFIER_RIGHT(2);

		private final int value;
		Modifier(int value) { this.value = value; }
		public int getValue() { return this.value; }

		public static Modifier forValue(int value) {
			for (Modifier m : Modifier.values()) {
				if (m.getValue() == value) {
					return m;
				}
			}

			LOGGER.warning("Unknown key stroke modifier for value " + value + ".");
			return KEY_MODIFIER_UNKNOWN;
		}

		private int getHashCode() {
			return value;
		}

		private boolean equivalent(Modifier other) {
			return (this == KEY_MODIFIER_UNKNOWN) || (other == KEY_MODIFIER_UNKNOWN) || (this == other);
		}
	}

	private int key;
	private Modifier modifier;

	private LocalDateTime invokedTime;
	private KeyboardState keyboardState;

	public static KeyStroke of(int key, Modifier modifier) {
		return new KeyStroke(key, modifier, LocalDateTime.now(), KeyboardState.getDefault());
	}

	public static KeyStroke of(int key, Modifier modifier, LocalDateTime invokedTime, KeyboardState keyboardState) {
		return new KeyStroke(key, modifier, invokedTime, keyboardState);
	}

	private KeyStroke(int key, Modifier modifier, LocalDateTime invokedTime, KeyboardState keyboardState) {
		this.key = key;
		this.modifier = modifier;

		this.invokedTime = invokedTime;
		this.keyboardState = keyboardState;
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
	public Modifier getModifier() {
		return modifier;
	}

	/**
	 * Syntactic sugar for {@link #getModifier()}.
	 */
	public Modifier m() {
		return getModifier();
	}

	public LocalDateTime getInvokedTime() {
		return invokedTime;
	}

	public KeyboardState getKeyboardState() {
		return keyboardState;
	}

	@Override
	public KeyStroke clone() {
		return of(key, modifier);
	}

	@Override
	public String toString() {
		String suffix = "";
		if (modifier == Modifier.KEY_MODIFIER_LEFT) {
			suffix = "(L)";
		}
		if (modifier == Modifier.KEY_MODIFIER_RIGHT) {
			suffix = "(R)";
		}
		return KeyEvent.getKeyText(getKey()) + suffix;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + key;
		result = prime * result + ((modifier == null) ? 0 : modifier.getHashCode());
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
		if (!modifier.equivalent(other.modifier)) {
			return false;
		}
		return true;
	}

	@Override
	public JsonRootNode jsonize() {
		return JsonNodeFactories.object(
				JsonNodeFactories.field("key", JsonNodeFactories.number(getKey())),
				JsonNodeFactories.field("modifier", JsonNodeFactories.number(getModifier().getValue()))
				);
	}

	public static KeyStroke parseJSON(JsonNode n) {
		if (n.isNumberValue()) {
			return of(Integer.parseInt(n.getNumberValue()), Modifier.KEY_MODIFIER_UNKNOWN);
		}

		int key = Integer.parseInt(n.getNumberValue("key"));
		int modifier = Integer.parseInt(n.getNumberValue("modifier"));
		return of(key, Modifier.forValue(modifier));
	}
}
