package org.simplenativehooks.osx;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;

import org.simplenativehooks.InvalidMouseEventException;
import org.simplenativehooks.NativeHookMouseEvent;
import org.simplenativehooks.NativeMouseEvent;
import org.simplenativehooks.NativeMouseEvent.State;

public class OSXNativeMouseEvent extends NativeHookMouseEvent {

	private int code;
	private int x, y;

	private OSXNativeMouseEvent(int code, int x, int y) {
		this.code = code;
		this.x = x;
		this.y = y;
	}

	public static OSXNativeMouseEvent of(int code, int x, int y) {
		return new OSXNativeMouseEvent(code, x, y);
	}

	@Override
	public NativeMouseEvent convertEvent() throws InvalidMouseEventException {
		int x = this.x;
		int y = this.y;
		State s = State.UNKNOWN;
		int button = 0;

		switch (code) {
		case 3:
			s = State.PRESSED;
			button = KeyEvent.BUTTON1_DOWN_MASK;
			break;
		case 4:
			s = State.RELEASED;
			button = KeyEvent.BUTTON3_DOWN_MASK;
			break;
		case 5:
			s = State.PRESSED;
			button = KeyEvent.BUTTON3_DOWN_MASK;
			break;
		case 6:
			s = State.RELEASED;
			button = KeyEvent.BUTTON3_DOWN_MASK;
			break;
		case 7:
			s = State.SCROLLED;
			Point p = MouseInfo.getPointerInfo().getLocation();
			x = p.x;
			y = p.y;
			break;
		case 8:
			s = State.MOVED;
			break;
		default:
			throw new InvalidMouseEventException("Unknown code '" + code + "' for OSX mouse event.");
		}

		return NativeMouseEvent.of(x, y, s, button);
	}
}
