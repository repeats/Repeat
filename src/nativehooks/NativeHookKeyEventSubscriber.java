package nativehooks;

import globalListener.NativeKeyEvent;

public interface NativeHookKeyEventSubscriber {
	public void processKeyboardEvent(NativeKeyEvent event);
}
