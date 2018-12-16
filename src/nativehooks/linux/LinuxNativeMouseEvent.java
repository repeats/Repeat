package nativehooks.linux;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.InputEvent;

import globalListener.NativeMouseEvent;
import globalListener.NativeMouseEvent.State;
import nativehooks.NativeHookMouseEvent;
import nativehooks.InvalidMouseEventException;

class LinuxNativeMouseEvent extends NativeHookMouseEvent {
	private final int type;
	private final int code;
	private final int value;

	private LinuxNativeMouseEvent(int type, int code, int value) {
		this.type = type;
		this.code = code;
		this.value = value;
	}

	protected static LinuxNativeMouseEvent of(int type, int code, int value) {
		return new LinuxNativeMouseEvent(type, code, value);
	}

	@Override
	public NativeMouseEvent convertEvent() throws InvalidMouseEventException {
		State s = State.UNKNOWN;
		int button = 0;
		Point p;

		switch (type) {
		case 1: // EV_KEY --> button click.
			switch (code) {
			case 0x110: // BTN_MOUSE, also BTN_LEFT
				button = InputEvent.BUTTON1_DOWN_MASK;
				break;
			case 0x111: // BTN_RIGHT
				button = InputEvent.BUTTON3_DOWN_MASK;
				break;
			case 0x112: // BTN_MIDDLE
				button = InputEvent.BUTTON2_DOWN_MASK;
				break;
			default:
				throw new InvalidMouseEventException("Unknown code '" + code + "' for button click on mouse.");
			}

			switch (value) {
			case 0: // Released.
				s = State.RELEASED;
				break;
			case 1: // Pressed.
				s = State.PRESSED;
				break;
			default:
				throw new InvalidMouseEventException("Unknown value '" + value + "' for button click on mouse.");
			}

			p = MouseInfo.getPointerInfo().getLocation();
			return NativeMouseEvent.of(p.x, p.y, s, button);
		case 2: // EV_REL --> mouse moved or scrolled.
			switch (code) {
			case 0x01: // REL_X
			case 0x02: // REL_Y
				p = MouseInfo.getPointerInfo().getLocation();
				s = State.MOVED;
				return NativeMouseEvent.of(p.x, p.y, s, button);
			case 0x06: // REL_HWHEEL
			case 0x08: // REL_WHEEL
				throw new InvalidMouseEventException("Not handling scrolling events.");
			default:
				throw new InvalidMouseEventException("Unknown code '" + code + "' for type '" + type + "'.");
			}
		case 3: // EV_ABS --> mouse moved.
			switch (code) {
			case 0x01: // REL_X
			case 0x02: // REL_Y
				p = MouseInfo.getPointerInfo().getLocation();
				s = State.MOVED;
				return NativeMouseEvent.of(p.x, p.y, s, button);
			default:
				throw new InvalidMouseEventException("Unknown code '" + code + "' for type '" + type + "'.");
			}
		default:
			throw new InvalidMouseEventException("Unknown type '" + type + ".");
		}
	}
}
