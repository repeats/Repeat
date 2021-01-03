package core.keyChain;

import core.userDefinedTask.internals.SharedVariablesEvent;

public class ActivationEvent {
	public static enum EventType {
		UNKNOWN("unknown"),
		BUTTON_STROKE("button_stroke"),
		SHARED_VARIABLE("shared_variable");

		private String value;

		private EventType(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}
	}

	private EventType type;
	private ButtonStroke buttonStroke;
	private SharedVariablesEvent variable;

	private ActivationEvent(ButtonStroke buttonStroke) {
		this.type = EventType.BUTTON_STROKE;
		this.buttonStroke = buttonStroke;
	}

	private ActivationEvent(SharedVariablesEvent variable) {
		this.type = EventType.SHARED_VARIABLE;
		this.variable = variable;
	}

	public static ActivationEvent of(ButtonStroke buttonStroke) {
		return new ActivationEvent(buttonStroke);
	}

	public static ActivationEvent of(SharedVariablesEvent variable) {
		return new ActivationEvent(variable);
	}

	public EventType getType() {
		return type;
	}

	public ButtonStroke getButtonStroke() {
		if (type != EventType.BUTTON_STROKE) {
			throw new IllegalStateException("Even is not button stroke but is type " + type + ".");
		}
		return buttonStroke;
	}

	public SharedVariablesEvent getVariable() {
		if (type != EventType.SHARED_VARIABLE) {
			throw new IllegalStateException("Even is not shared variable but is type " + type + ".");
		}
		return variable;
	}
}
