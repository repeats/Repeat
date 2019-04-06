package org.simplenativehooks.listeners;

import org.simplenativehooks.events.NativeKeyEvent;

import utilities.Function;

public abstract class AbstractGlobalKeyListener implements GlobalListener {
	protected Function<NativeKeyEvent, Boolean> keyPressed;
	protected Function<NativeKeyEvent, Boolean> keyReleased;

	protected AbstractGlobalKeyListener() {
		keyPressed = Function.<NativeKeyEvent>trueFunction();
		keyReleased = Function.<NativeKeyEvent>trueFunction();
	}

	public final void setKeyPressed(Function<NativeKeyEvent, Boolean> keyPressed) {
		this.keyPressed = keyPressed;
	}

	public final void setKeyReleased(Function<NativeKeyEvent, Boolean> keyReleased) {
		this.keyReleased = keyReleased;
	}
}
