package core.keyChain.managers;

import java.util.HashSet;
import java.util.Set;

import core.config.Config;
import core.keyChain.ActivationEvent;
import core.keyChain.ActivationEvent.EventType;
import core.keyChain.KeyStroke;
import core.userDefinedTask.UserDefinedAction;

public abstract class KeyStrokeManager extends ActivationEventManager {

	public KeyStrokeManager(Config config) {
		super(config);
	}

	@Override
	public final Set<UserDefinedAction> onActivationEvent(ActivationEvent event) {
		if (event.getType() != EventType.KEY_STROKE) {
			return new HashSet<>();
		}

		KeyStroke keyStroke = event.getKeyStroke();
		if (keyStroke.isPressed()) {
			return onKeyStrokePressed(keyStroke);
		}
		return onKeyStrokeReleased(keyStroke);
	}

	public abstract Set<UserDefinedAction> onKeyStrokePressed(KeyStroke stroke);
	public abstract Set<UserDefinedAction> onKeyStrokeReleased(KeyStroke stroke);
}
