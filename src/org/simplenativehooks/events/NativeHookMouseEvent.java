package org.simplenativehooks.events;

public abstract class NativeHookMouseEvent {
	public abstract NativeMouseEvent convertEvent() throws InvalidMouseEventException;
}
