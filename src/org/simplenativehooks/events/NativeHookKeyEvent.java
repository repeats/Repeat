package org.simplenativehooks.events;

public abstract class NativeHookKeyEvent {
	public abstract NativeKeyEvent convertEvent() throws InvalidKeyEventException;
}
