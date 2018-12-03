package nativehooks;

import globalListener.NativeKeyEvent;

public abstract class NativeHookKeyEvent {
	public abstract NativeKeyEvent convertEvent() throws UnknownKeyEventException;
}
