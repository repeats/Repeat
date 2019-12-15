package core.keyChain;

import core.userDefinedTask.internals.SharedVariablesEvent;

public class ActivationEvent {
	public static enum EventType {
		UNKNOWN("unknown"),
		KEY_STROKE("key_stroke"),
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
	private KeyStroke keyStroke;
	private SharedVariablesEvent variable;

	private ActivationEvent(KeyStroke keyStroke) {
		this.type = EventType.KEY_STROKE;
		this.keyStroke = keyStroke;
	}

	private ActivationEvent(SharedVariablesEvent variable) {
		this.type = EventType.SHARED_VARIABLE;
		this.variable = variable;
	}

	public static ActivationEvent of(KeyStroke keyStroke) {
		return new ActivationEvent(keyStroke);
	}

	public static ActivationEvent of(SharedVariablesEvent variable) {
		return new ActivationEvent(variable);
	}

	public EventType getType() {
		return type;
	}

	public KeyStroke getKeyStroke() {
		if (type != EventType.KEY_STROKE) {
			throw new IllegalStateException("Even is not key stroke but is type " + type + ".");
		}
		return keyStroke;
	}

	public SharedVariablesEvent getVariable() {
		if (type != EventType.SHARED_VARIABLE) {
			throw new IllegalStateException("Even is not shared variable but is type " + type + ".");
		}
		return variable;
	}
}
