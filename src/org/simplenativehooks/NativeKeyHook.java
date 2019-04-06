package org.simplenativehooks;

import java.util.logging.Logger;

import org.simplenativehooks.events.NativeKeyEvent;
import org.simplenativehooks.listeners.AbstractGlobalKeyListener;

public final class NativeKeyHook extends AbstractGlobalKeyListener implements NativeHookKeyEventSubscriber {

	private static final Logger LOGGER = Logger.getLogger(NativeKeyHook.class.getName());
	private NativeKeyHook() {}

	public static NativeKeyHook of() {
		return new NativeKeyHook();
	}

	@Override
	public boolean startListening() {
		NativeHookGlobalEventPublisher.of().addKeyEventSubscriber(this);
		return true;
	}

	@Override
	public boolean stopListening() {
		NativeHookGlobalEventPublisher.of().removeKeyEventSubscriber(this);
		return true;
	}

	@Override
	public void processKeyboardEvent(NativeKeyEvent event) {
		if (event.getKeyStroke().isPressed()) {
			if (!keyPressed.apply(event)) {
				LOGGER.warning("Failed to process key press event.");
			}
		} else {
			if (!keyReleased.apply(event)) {
				LOGGER.warning("Failed to process key release event.");
			}
		}
	}

}
