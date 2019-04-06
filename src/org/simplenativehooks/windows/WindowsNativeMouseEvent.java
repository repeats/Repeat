package org.simplenativehooks.windows;

import java.awt.event.InputEvent;

import org.simplenativehooks.events.InvalidMouseEventException;
import org.simplenativehooks.events.NativeHookMouseEvent;
import org.simplenativehooks.events.NativeMouseEvent;
import org.simplenativehooks.events.NativeMouseEvent.State;

class WindowsNativeMouseEvent extends NativeHookMouseEvent {
	private final int x;
	private final int y;

	private final int code;

	private WindowsNativeMouseEvent(int x, int y, int code) {
		this.x = x;
		this.y = y;
		this.code = code;
	}

	protected static WindowsNativeMouseEvent of(int x, int y, int code) {
		return new WindowsNativeMouseEvent(x, y, code);
	}

	@Override
	public NativeMouseEvent convertEvent() throws InvalidMouseEventException {
		State s;
		int button;

		switch (code) {
		case 512:
			s = State.MOVED;
			button = 0;
			break;
		case 513:
			s = State.PRESSED;
			button = InputEvent.BUTTON1_DOWN_MASK;
			break;
		case 514:
			s = State.RELEASED;
			button = InputEvent.BUTTON1_DOWN_MASK;
			break;
		case 516:
			s = State.PRESSED;
			button = InputEvent.BUTTON3_DOWN_MASK;
			break;
		case 517:
			s = State.RELEASED;
			button = InputEvent.BUTTON3_DOWN_MASK;
			break;
		case 519:
			s = State.PRESSED;
			button = InputEvent.BUTTON2_DOWN_MASK;
			break;
		case 520:
			s = State.RELEASED;
			button = InputEvent.BUTTON2_DOWN_MASK;
			break;
		case 522:
			s = State.SCROLLED;
			button = 0;
			break;
		default:
			throw new InvalidMouseEventException("Unknown code " + code + ".");
		}

		return NativeMouseEvent.of(x, y, s, button);
	}
}
