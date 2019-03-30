package org.simplenativehooks;

public abstract class NativeHookKeyEvent {
	public abstract NativeKeyEvent convertEvent() throws InvalidKeyEventException;
}
