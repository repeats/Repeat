package core.keyChain;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import utilities.json.IJsonable;

/**
 * Represents that this activation is configured to comes from all key/mouse events.
 */
public class GlobalActivation implements IJsonable {

	private static final Logger LOGGER = Logger.getLogger(GlobalActivation.class.getName());

	private boolean onKeyReleased;
	private boolean onKeyPressed;

	private GlobalActivation(Builder builder) {
		this.onKeyReleased = builder.onKeyReleased;
		this.onKeyPressed = builder.onKeyPressed;
	}

	public boolean isOnKeyReleased() {
		return onKeyReleased;
	}

	public boolean isOnKeyPressed() {
		return onKeyPressed;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	protected static class Builder {
		private boolean onKeyReleased;
		private boolean onKeyPressed;

		private Builder() {}

		public static Builder fromGlobalActivation(GlobalActivation other) {
			Builder builder = new Builder();
			builder.onKeyReleased = other.onKeyReleased;
			builder.onKeyPressed = other.onKeyPressed;
			return builder;
		}

		public Builder onKeyReleased(boolean value) {
			onKeyReleased = value;
			return this;
		}

		public Builder onKeyPressed(boolean value) {
			onKeyPressed = value;
			return this;
		}

		public GlobalActivation build() {
			return new GlobalActivation(this);
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(onKeyPressed, onKeyReleased);
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
		GlobalActivation other = (GlobalActivation) obj;
		if (onKeyPressed != other.onKeyPressed) {
			return false;
		}
		if (onKeyReleased != other.onKeyReleased) {
			return false;
		}
		return true;
	}

	@Override
	public JsonRootNode jsonize() {
		return JsonNodeFactories.object(
				JsonNodeFactories.field("on_key_released", JsonNodeFactories.booleanNode(onKeyReleased)),
				JsonNodeFactories.field("on_key_pressed", JsonNodeFactories.booleanNode(onKeyPressed)));
	}

	public static GlobalActivation parseJSON(JsonNode node) {
		try {
			boolean onKeyReleased = node.getBooleanValue("on_key_released");
			boolean onKeyPressed = node.getBooleanValue("on_key_pressed");
			return new Builder().onKeyPressed(onKeyReleased).onKeyPressed(onKeyPressed).build();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to parse global activation.", e);
			return null;
		}
	}
}
