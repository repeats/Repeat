package nativehooks;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import globalListener.NativeKeyEvent;
import globalListener.NativeMouseEvent;

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
		for (NativeHookMouseEventSubscriber subscriber : subscriberss) {
			try {
				NativeMouseEvent mouseEvent = event.convertEvent();
				subscriber.processMouseEvent(mouseEvent);
			} catch (UnknownMouseEventException e) {
				LOGGER.log(Level.INFO, "Dropping mouse event due to exception.\n" + e.getError(), e);
			}
		}
	}

	public void publishKeyEvent(NativeHookKeyEvent event) {
		for (NativeHookKeyEventSubscriber subscriber : subscribers) {
			try {
				NativeKeyEvent keyEvent = event.convertEvent();
				subscriber.processKeyboardEvent(keyEvent);
			} catch (UnknownKeyEventException e) {
				LOGGER.log(Level.INFO, "Dropping key event due to exception.\n" + e.getError(), e);
			}
		}
	}
}
