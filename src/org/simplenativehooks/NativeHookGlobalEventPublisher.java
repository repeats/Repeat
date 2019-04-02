package org.simplenativehooks;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NativeHookGlobalEventPublisher {

	private static final Logger LOGGER = Logger.getLogger(NativeHookGlobalEventPublisher.class.getName());
	private static final NativeHookGlobalEventPublisher INSTANCE = new NativeHookGlobalEventPublisher();

	private NativeHookGlobalEventPublisher() {
		this.subscribers = new LinkedList<>();
		this.subscriberss = new LinkedList<>();
	}

	private List<NativeHookKeyEventSubscriber> subscribers;
	private List<NativeHookMouseEventSubscriber> subscriberss;

	public static NativeHookGlobalEventPublisher of() {
		return INSTANCE;
	}

	public void addKeyEventSubscriber(NativeHookKeyEventSubscriber subscriber) {
		this.subscribers.add(subscriber);
	}

	public void removeKeyEventSubscriber(NativeHookKeyEventSubscriber subscriber) {
		this.subscribers.remove(subscriber);
	}

	public void addMouseEventSubscriber(NativeHookMouseEventSubscriber subscriber) {
		this.subscriberss.add(subscriber);
	}

	public void removeMouseEventSubscriber(NativeHookMouseEventSubscriber subscriber) {
		this.subscriberss.remove(subscriber);
	}

	public void publishMouseEvent(NativeHookMouseEvent event) {
		NativeMouseEvent mouseEvent;
		try {
			mouseEvent = event.convertEvent();
		} catch (InvalidMouseEventException e) {
			LOGGER.log(Level.FINE, "Dropping mouse event due to exception.\n" + e.getError(), e);
			return;
		}
		for (NativeHookMouseEventSubscriber subscriber : subscriberss) {
			subscriber.processMouseEvent(mouseEvent);
		}
	}

	public void publishKeyEvent(NativeHookKeyEvent event) {
		NativeKeyEvent keyEvent;
		try {
			keyEvent = event.convertEvent();
		} catch (InvalidKeyEventException e) {
			LOGGER.log(Level.FINE, "Dropping key event due to exception.\n" + e.getError(), e);
			return;
		}
		for (NativeHookKeyEventSubscriber subscriber : subscribers) {
			subscriber.processKeyboardEvent(keyEvent);
		}
	}
}
