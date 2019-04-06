package org.simplenativehooks.events;

import core.keyChain.KeyStroke;

public class NativeKeyEvent {
	private KeyStroke keyStroke;

	private NativeKeyEvent(KeyStroke keyStroke) {
		this.keyStroke = keyStroke;
	}

	public static NativeKeyEvent of(KeyStroke keyStroke) {
		return new NativeKeyEvent(keyStroke);
	}

	public KeyStroke getKeyStroke() {
		return keyStroke;
	}
}
