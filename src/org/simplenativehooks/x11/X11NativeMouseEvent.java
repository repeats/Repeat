package org.simplenativehooks.x11;

import java.awt.event.KeyEvent;
import java.util.concurrent.locks.ReentrantLock;

import org.simplenativehooks.InvalidMouseEventException;
import org.simplenativehooks.NativeHookMouseEvent;

import globalListener.NativeMouseEvent;
import globalListener.NativeMouseEvent.State;

public class X11NativeMouseEvent extends NativeHookMouseEvent {

	private static int currentX, currentY;
	private static final ReentrantLock coordinateLock = new ReentrantLock();

	private int x;
	private int y;

	private String event;
	private int button;

	private X11NativeMouseEvent(int x, int y) {
		this.x = x;
		this.y = y;
	}

	private X11NativeMouseEvent(String event, int button) {
		this.event = event;
		this.button = button;
	}

	public static X11NativeMouseEvent of(int x, int y) {
		return new X11NativeMouseEvent(x, y);
	}

	public static X11NativeMouseEvent of(String event, int button) {
		return new X11NativeMouseEvent(event, button);
	}

	@Override
	public NativeMouseEvent convertEvent() throws InvalidMouseEventException {
		if (event == null) { // This is a mouse move event.
			updatePosition(x, y);
			return NativeMouseEvent.of(x, y, State.MOVED, 0);
		}

		try {
			coordinateLock.lock();
			x = currentX;
			y = currentY;
		} finally {
			coordinateLock.unlock();
		}

		int b = 0;
		State s = State.UNKNOWN;
		switch (event) {
		case "P":
			s = State.PRESSED;
			break;
		case "R":
			s = State.RELEASED;
			break;
		default:
			throw new InvalidMouseEventException("Unknown event '" + button + "'.");
		}

		switch (button) {
		case 1: // Left.
			b = KeyEvent.BUTTON1_DOWN_MASK;
			break;
		case 2: // Middle.
			b = KeyEvent.BUTTON2_DOWN_MASK;
			break;
		case 3: // Right.
			b = KeyEvent.BUTTON3_DOWN_MASK;
			break;
		case 4: // Scrolled up.
			s = State.SCROLLED;
			break;
		case 5: // Scrolled down.
			s = State.SCROLLED;
			break;
		default:
			throw new InvalidMouseEventException("Unknown button '" + button + "'.");
		}

		return NativeMouseEvent.of(x, y, s, b);
	}

	private static void updatePosition(int x, int y) {
		try {
			coordinateLock.lock();
			currentX = x;
			currentY = y;
		} finally {
			coordinateLock.unlock();
		}
	}
}
