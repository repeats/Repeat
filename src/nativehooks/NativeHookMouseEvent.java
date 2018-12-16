package nativehooks;

import globalListener.NativeMouseEvent;

public abstract class NativeHookMouseEvent {
	public abstract NativeMouseEvent convertEvent() throws InvalidMouseEventException;
}
