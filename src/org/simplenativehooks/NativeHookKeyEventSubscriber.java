package org.simplenativehooks;

import org.simplenativehooks.events.NativeKeyEvent;

public interface NativeHookKeyEventSubscriber {
	public void processKeyboardEvent(NativeKeyEvent event);
}
