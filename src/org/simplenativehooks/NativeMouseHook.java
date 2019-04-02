package org.simplenativehooks;

import java.util.logging.Logger;

import globalListener.AbstractGlobalMouseListener;

public class NativeMouseHook extends AbstractGlobalMouseListener implements NativeHookMouseEventSubscriber {

	private static final Logger LOGGER = Logger.getLogger(NativeMouseHook.class.getName());
	private NativeMouseHook() {}

	public static NativeMouseHook of() {
		return new NativeMouseHook();
	}

	@Override
	public boolean startListening() {
		NativeHookGlobalEventPublisher.of().addMouseEventSubscriber(this);
		return true;
	}

	@Override
	public boolean stopListening() {
		NativeHookGlobalEventPublisher.of().removeMouseEventSubscriber(this);
		return true;
	}

	@Override
	public void processMouseEvent(NativeMouseEvent event) {
		if (event.getState().equals(NativeMouseEvent.State.MOVED)) {
			if (!mouseMoved.apply(event)) {
				LOGGER.warning("Failed to process mouse moved event.");
			}
		} else if (event.getState().equals(NativeMouseEvent.State.PRESSED)) {
			if (!mousePressed.apply(event)) {
				LOGGER.warning("Failed to process mouse press event.");
			}
		} else if (event.getState().equals(NativeMouseEvent.State.RELEASED)) {
			if (!mouseReleased.apply(event)) {
				LOGGER.warning("Failed to process mouse release event.");
			}
		} else { // Drop
			LOGGER.finer("Silently dropping mouse event with unknown state.");
		}
	}

}
