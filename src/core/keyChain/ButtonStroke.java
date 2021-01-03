package core.keyChain;

import argo.jdom.JsonNode;
import utilities.json.IJsonable;

/**
 * Represents any input button stroke (e.g. mouse button, keyboard, joystick, ...)
 */
public interface ButtonStroke extends IJsonable {
	public int getKey();

	/**
	 * Syntactic sugar for {@link #getKey()}.
	 */
	public default int k() {
		return getKey();
	}

	public boolean isPressed();
	public KeyboardResult getTypedString(KeyboardState keyboardState);
	public ButtonStroke clone();

	public static ButtonStroke parseJSON(JsonNode n) {
		if (n.isStringValue("type") && n.getStringValue("type").equals(MouseKey.TYPE_STRING)) {
			return MouseKey.parseJSON(n);
		}
		return KeyStroke.parseJSON(n);
	}

	public static class KeyboardResult {
		private KeyboardState keyboardState;
		private String typedString;

		static KeyboardResult of(KeyboardState keyboardState, String typedString) {
			KeyboardResult result = new KeyboardResult();
			result.keyboardState = keyboardState;
			result.typedString = typedString;

			return result;
		}

		public KeyboardState keyboardState() {
			return keyboardState;
		}

		public String typedString() {
			return typedString;
		}
	}
}
