package org.simplenativehooks;

public abstract class NativeHookMouseEvent {
	public abstract NativeMouseEvent convertEvent() throws InvalidMouseEventException;
}
