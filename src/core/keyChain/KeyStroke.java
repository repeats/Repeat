package core.keyChain;

import java.time.LocalDateTime;
import java.util.logging.Logger;

import org.simplenativehooks.events.NativeKeyEvent;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import utilities.KeyCodeToChar;
import utilities.KeyEventCodeToString;

/**
 * Represents a key stroke on the keyboard.
 */
public class KeyStroke implements ButtonStroke {

	private static final Logger LOGGER = Logger.getLogger(KeyStroke.class.getName());

	static final String TYPE_STRING = "key_stroke";

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

		public org.simplenativehooks.events.NativeKeyEvent.Modifier toNativeModifier() {
			switch (this) {
			case KEY_MODIFIER_UNKNOWN:
				return org.simplenativehooks.events.NativeKeyEvent.Modifier.KEY_MODIFIER_UNKNOWN;
			case KEY_MODIFIER_LEFT:
				return org.simplenativehooks.events.NativeKeyEvent.Modifier.KEY_MODIFIER_LEFT;
			case KEY_MODIFIER_RIGHT:
				return org.simplenativehooks.events.NativeKeyEvent.Modifier.KEY_MODIFIER_RIGHT;
			}
			throw new IllegalArgumentException("Unknown modifier " + this);
		}
	}

	private int key;
	private Modifier modifier;

	private boolean pressed; // Press or release.
	private LocalDateTime invokedTime;

	public static KeyStroke of(int key, Modifier modifier) {
		return new KeyStroke(key, modifier, false, LocalDateTime.now());
	}

	public static KeyStroke of(int key, Modifier modifier, boolean press, LocalDateTime invokedTime) {
		return new KeyStroke(key, modifier, press, invokedTime);
	}

	public static KeyStroke of(NativeKeyEvent e) {
		Modifier m = Modifier.KEY_MODIFIER_UNKNOWN;
		switch (e.getModifier()) {
		case KEY_MODIFIER_UNKNOWN:
			m = Modifier.KEY_MODIFIER_UNKNOWN;
			break;
		case KEY_MODIFIER_LEFT:
			m = Modifier.KEY_MODIFIER_LEFT;
			break;
		case KEY_MODIFIER_RIGHT:
			m = Modifier.KEY_MODIFIER_RIGHT;
			break;
		default:
			m = Modifier.KEY_MODIFIER_UNKNOWN;
			break;
		}

		return of(e.getKey(), m, e.isPressed(), e.getInvokedTime());
	}

	public NativeKeyEvent toNativeKeyEvent() {
		return NativeKeyEvent.of(key, modifier.toNativeModifier(), pressed);
	}

	private KeyStroke(int key, Modifier modifier, boolean press, LocalDateTime invokedTime) {
		this.key = key;
		this.modifier = modifier;
		this.pressed = press;
		this.invokedTime = invokedTime;
	}

	/**
	 * Retrieve the key on the keyboard. This alone does not identify the exact key
	 * for cases like Ctrl and Shift, which have left and right keys.
	 *
	 * @return the integer representing the key on the keyboard.
	 */
	@Override
	public int getKey() {
		return key;
	}

	@Override
	public Source getSource() {
		return Source.KEYBOARD;
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

	public KeyStroke at(LocalDateTime invokedTime) {
		this.invokedTime = invokedTime;
		return this;
	}

	public KeyStroke press(boolean pressed) {
		this.pressed = pressed;
		return this;
	}

	@Override
	public boolean isPressed() {
		return pressed;
	}

	@Override
	public KeyStroke clone() {
		return of(key, modifier, pressed, invokedTime);
	}

	@Override
	public String toString() {
		String suffix = "";
		if (modifier == Modifier.KEY_MODIFIER_LEFT) {
			suffix = " (L)";
		}
		if (modifier == Modifier.KEY_MODIFIER_RIGHT) {
			suffix = " (R)";
		}
		return KeyEventCodeToString.codeToString(getKey()) + suffix;
	}

	@Override
	public KeyboardResult getTypedString(KeyboardState keyboardState) {
		keyboardState = keyboardState.changeWith(this);
		String s = KeyCodeToChar.getCharForCode(getKey(), keyboardState);

		return KeyboardResult.of(keyboardState, s);
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
				JsonNodeFactories.field("type", JsonNodeFactories.string(TYPE_STRING)),
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
