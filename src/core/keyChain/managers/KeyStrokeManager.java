package core.keyChain.managers;

import java.util.HashSet;
import java.util.Set;

import core.config.Config;
import core.keyChain.ActivationEvent;
import core.keyChain.ActivationEvent.EventType;
import core.keyChain.ButtonStroke;
import core.userDefinedTask.UserDefinedAction;

public abstract class KeyStrokeManager extends ActivationEventManager {

	public KeyStrokeManager(Config config) {
		super(config);
	}

	@Override
	public final Set<UserDefinedAction> onActivationEvent(ActivationEvent event) {
		if (event.getType() != EventType.BUTTON_STROKE) {
			return new HashSet<>();
		}

		ButtonStroke buttonStroke = event.getButtonStroke();
		if (buttonStroke.isPressed()) {
			return onButtonStrokePressed(buttonStroke);
		}
		return onButtonStrokeReleased(buttonStroke);
	}

	public abstract Set<UserDefinedAction> onButtonStrokePressed(ButtonStroke stroke);
	public abstract Set<UserDefinedAction> onButtonStrokeReleased(ButtonStroke stroke);
}
