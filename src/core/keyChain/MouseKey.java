package core.keyChain;

import java.awt.event.InputEvent;

import org.simplenativehooks.events.NativeMouseEvent;
import org.simplenativehooks.events.NativeMouseEvent.State;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;

public class MouseKey implements ButtonStroke {

	static final String TYPE_STRING = "mouse_key";

	private int key;
	private boolean isPressed;

	private MouseKey(int key, boolean isPressed) {
		this.key = key;
		this.isPressed = isPressed;
	}

	public static MouseKey of(int key) {
		return new MouseKey(key, false);
	}

	public static MouseKey of(int key, boolean isPressed) {
		return new MouseKey(key, isPressed);
	}

	public static MouseKey of(NativeMouseEvent mouseEvent) {
		State s = mouseEvent.getState();
		if (s != State.PRESSED && s != State.RELEASED) {
			throw new IllegalStateException("Constructing mouse key with mouse event with state " + s + ". Want either pressed or released.");
		}

		return new MouseKey(mouseEvent.getButton(), mouseEvent.getState() == State.PRESSED);
	}

	@Override
	public int getKey() {
		return key;
	}

	@Override
	public boolean isPressed() {
		return isPressed;
	}

	@Override
	public JsonRootNode jsonize() {
		return JsonNodeFactories.object(
				JsonNodeFactories.field("type", JsonNodeFactories.string(TYPE_STRING)),
				JsonNodeFactories.field("key", JsonNodeFactories.number(getKey())));
	}

	public static MouseKey parseJSON(JsonNode n) {
		if (n.isNumberValue()) {
			return of(Integer.parseInt(n.getNumberValue()));
		}

		int key = Integer.parseInt(n.getNumberValue("key"));
		return of(key);
	}

	@Override
	public MouseKey clone() {
		return of(key, isPressed);
	}

	@Override
	public String toString() {
		if (key == InputEvent.BUTTON1_DOWN_MASK) {
			return "Mouse (L)";
		}
		if (key == InputEvent.BUTTON2_DOWN_MASK) {
			return "Mouse (M)";
		}
		if (key == InputEvent.BUTTON3_DOWN_MASK) {
			return "Mouse (R)";
		}
		return "Unknown mouse button (" + key + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + key;
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
		MouseKey other = (MouseKey) obj;
		if (key != other.key) {
			return false;
		}
		return true;
	}

	@Override
	public KeyboardResult getTypedString(KeyboardState keyboardState) {
		return KeyboardResult.of(keyboardState, "");
	}
}
