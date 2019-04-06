package org.simplenativehooks;

import org.simplenativehooks.events.NativeMouseEvent;

public interface NativeHookMouseEventSubscriber {
	public void processMouseEvent(NativeMouseEvent event);
}
