package org.simplenativehooks.osx;

import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.simplenativehooks.events.InvalidKeyEventException;
import org.simplenativehooks.events.NativeHookKeyEvent;
import org.simplenativehooks.events.NativeKeyEvent;

import core.keyChain.KeyStroke;
import core.keyChain.KeyStroke.Modifier;

public class OSXNativeKeyEvent extends NativeHookKeyEvent {

	// The modifier in OSX is complicated enough that it is easier
	// to just track the flag being enabled here.
	private static final ReentrantLock flagLock = new ReentrantLock();
	private static final Set<Integer> keyboardFlags = new HashSet<>();

	private int event;
	private int code;
	private long modifier;
	private boolean processed;

	private OSXNativeKeyEvent(int event, int code, long modifier) {
		this.event = event;
		this.code = code;
		this.modifier = modifier;
	}

	public static OSXNativeKeyEvent of(int event, int code, long modifier) {
		return new OSXNativeKeyEvent(event, code, modifier);
	}

	@Override
	public NativeKeyEvent convertEvent() throws InvalidKeyEventException {
		checkProcessed();

		int c = KeyEvent.VK_UNDEFINED;
		Modifier m = Modifier.KEY_MODIFIER_UNKNOWN;

		switch (event) {
		case 0:
			switch (code) {
			case 54:
				c = KeyEvent.VK_META;
				m = Modifier.KEY_MODIFIER_RIGHT;
			case 55:
				c = KeyEvent.VK_META;
				m = Modifier.KEY_MODIFIER_LEFT;
				break;
			case 56:
				c = KeyEvent.VK_SHIFT;
				m = Modifier.KEY_MODIFIER_LEFT;
				break;
			case 57:
				c = KeyEvent.VK_CAPS_LOCK;
				// In OSX there's no way to detect whether Capslock is pressed or released.
				// This event triggers only when the Capslock is turned off.
				return NativeKeyEvent.of(KeyStroke.of(c, m, false, LocalDateTime.now()));
			case 58:
				c = KeyEvent.VK_ALT;
				m = Modifier.KEY_MODIFIER_LEFT;
				break;
			case 59:
				c = KeyEvent.VK_CONTROL;
				m = Modifier.KEY_MODIFIER_LEFT;
				break;
			case 60:
				c = KeyEvent.VK_SHIFT;
				m = Modifier.KEY_MODIFIER_RIGHT;
				break;
			case 61:
				c = KeyEvent.VK_ALT;
				m = Modifier.KEY_MODIFIER_RIGHT;
				break;
//			case 63:
//				c = KeyEvent.VK_FUNCTION;
//				break;
			default:
				throw new InvalidKeyEventException("Unknown flags code '" + code + "' for OSX native key event.");
			}
			break;
		case 1:
			return getKeyEventForRegularKey(true);
		case 2:
			return getKeyEventForRegularKey(false);
		default:
			throw new InvalidKeyEventException("Unknown event '" + event + "' for OSX native key event.");
		}
		boolean pressed = getPressedForFlags(c);

		return NativeKeyEvent.of(KeyStroke.of(c, m, pressed, LocalDateTime.now()));
	}

	private NativeKeyEvent getKeyEventForRegularKey(boolean pressed) throws InvalidKeyEventException {
		int c = KeyEvent.VK_UNDEFINED;
		Modifier m = Modifier.KEY_MODIFIER_UNKNOWN;

		switch (code) {
		case 0x00:
			c = KeyEvent.VK_A;
			break;
		case 0x01:
			c = KeyEvent.VK_S;
			break;
		case 0x02:
			c = KeyEvent.VK_D;
			break;
		case 0x03:
			c = KeyEvent.VK_F;
			break;
		case 0x04:
			c = KeyEvent.VK_H;
			break;
		case 0x05:
			c = KeyEvent.VK_G;
			break;
		case 0x06:
			c = KeyEvent.VK_Z;
			break;
		case 0x07:
			c = KeyEvent.VK_X;
			break;
		case 0x08:
			c = KeyEvent.VK_C;
			break;
		case 0x09:
			c = KeyEvent.VK_V;
			break;
		case 0x0B:
			c = KeyEvent.VK_B;
			break;
		case 0x0C:
			c = KeyEvent.VK_Q;
			break;
		case 0x0D:
			c = KeyEvent.VK_W;
			break;
		case 0x0E:
			c = KeyEvent.VK_E;
			break;
		case 0x0F:
			c = KeyEvent.VK_R;
			break;
		case 0x10:
			c = KeyEvent.VK_Y;
			break;
		case 0x11:
			c = KeyEvent.VK_T;
			break;
		case 0x12:
			c = KeyEvent.VK_1;
			break;
		case 0x13:
			c = KeyEvent.VK_2;
			break;
		case 0x14:
			c = KeyEvent.VK_3;
			break;
		case 0x15:
			c = KeyEvent.VK_4;
			break;
		case 0x16:
			c = KeyEvent.VK_6;
			break;
		case 0x17:
			c = KeyEvent.VK_5;
			break;
		case 0x18:
			c = KeyEvent.VK_EQUALS;
			break;
		case 0x19:
			c = KeyEvent.VK_9;
			break;
		case 0x1A:
			c = KeyEvent.VK_7;
			break;
		case 0x1B:
			c = KeyEvent.VK_MINUS;
			break;
		case 0x1C:
			c = KeyEvent.VK_8;
			break;
		case 0x1D:
			c = KeyEvent.VK_0;
			break;
		case 0x1E:
			c = KeyEvent.VK_BRACERIGHT;
			break;
		case 0x1F:
			c = KeyEvent.VK_O;
			break;
		case 0x20:
			c = KeyEvent.VK_U;
			break;
		case 0x21:
			c = KeyEvent.VK_BRACELEFT;
			break;
		case 0x22:
			c = KeyEvent.VK_I;
			break;
		case 0x23:
			c = KeyEvent.VK_P;
			break;
		case 0x24:
			c = KeyEvent.VK_ENTER;
			m = Modifier.KEY_MODIFIER_LEFT;
			break;
		case 0x25:
			c = KeyEvent.VK_L;
			break;
		case 0x26:
			c = KeyEvent.VK_J;
			break;
		case 0x27:
			c = KeyEvent.VK_QUOTE;
			break;
		case 0x28:
			c = KeyEvent.VK_K;
			break;
		case 0x29:
			c = KeyEvent.VK_SEMICOLON;
			break;
		case 0x2A:
			c = KeyEvent.VK_BACK_SLASH;
			break;
		case 0x2B:
			c = KeyEvent.VK_COMMA;
			break;
		case 0x2C:
			c = KeyEvent.VK_SLASH;
			break;
		case 0x2D:
			c = KeyEvent.VK_N;
			break;
		case 0x2E:
			c = KeyEvent.VK_M;
			break;
		case 0x2F:
			c = KeyEvent.VK_PERIOD;
			break;
		case 0x30:
			c = KeyEvent.VK_TAB;
			break;
		case 0x31:
			c = KeyEvent.VK_SPACE;
			break;
		case 0x32:
			c = KeyEvent.VK_DEAD_TILDE;
			break;
		case 0x33:
			c = KeyEvent.VK_DELETE;
			break;
		case 0x35:
			c = KeyEvent.VK_ESCAPE;
			break;
		case 0x37:
			c = KeyEvent.VK_META;
			break;
		case 0x38:
			c = KeyEvent.VK_SHIFT;
			break;
		case 0x39:
			c = KeyEvent.VK_CAPS_LOCK;
			break;
		case 0x3A:
			c = KeyEvent.VK_ALT;
			break;
		case 0x3B:
			c = KeyEvent.VK_CONTROL;
			break;
		case 0x41:
			c = KeyEvent.VK_DECIMAL;
			break;
		case 0x43:
			c = KeyEvent.VK_MULTIPLY;
			break;
		case 0x45:
			c = KeyEvent.VK_PLUS;
			break;
		case 0x4B:
			c = KeyEvent.VK_DIVIDE;
			break;
		case 0x4C:
			c = KeyEvent.VK_ENTER;
			m = Modifier.KEY_MODIFIER_RIGHT;
			break;
		case 0x4E:
			c = KeyEvent.VK_SUBTRACT;
			break;
		case 0x51:
			c = KeyEvent.VK_EQUALS; // This is on numpad.
			m = Modifier.KEY_MODIFIER_RIGHT;
			break;
		case 0x52:
			c = KeyEvent.VK_NUMPAD0;
			break;
		case 0x53:
			c = KeyEvent.VK_NUMPAD1;
			break;
		case 0x54:
			c = KeyEvent.VK_NUMPAD2;
			break;
		case 0x55:
			c = KeyEvent.VK_NUMPAD3;
			break;
		case 0x56:
			c = KeyEvent.VK_NUMPAD4;
			break;
		case 0x57:
			c = KeyEvent.VK_NUMPAD5;
			break;
		case 0x58:
			c = KeyEvent.VK_NUMPAD6;
			break;
		case 0x59:
			c = KeyEvent.VK_NUMPAD7;
			break;
		case 0x5B:
			c = KeyEvent.VK_NUMPAD8;
			break;
		case 0x5C:
			c = KeyEvent.VK_NUMPAD9;
			break;
		case 0x60:
			c = KeyEvent.VK_F5;
			break;
		case 0x61:
			c = KeyEvent.VK_F6;
			break;
		case 0x62:
			c = KeyEvent.VK_F7;
			break;
		case 0x63:
			c = KeyEvent.VK_F3;
			break;
		case 0x64:
			c = KeyEvent.VK_F8;
			break;
		case 0x65:
			c = KeyEvent.VK_F9;
			break;
		case 0x67:
			c = KeyEvent.VK_F11;
			break;
		case 0x69:
			c = KeyEvent.VK_F13;
			break;
		case 0x6B:
			c = KeyEvent.VK_F14;
			break;
		case 0x6D:
			c = KeyEvent.VK_F10;
			break;
		case 0x6F:
			c = KeyEvent.VK_F12;
			break;
		case 0x71:
			c = KeyEvent.VK_F15;
			break;
		case 0x72:
			c = KeyEvent.VK_HELP;
			break;
		case 0x73:
			c = KeyEvent.VK_HOME;
			break;
		case 0x74:
			c = KeyEvent.VK_PAGE_UP;
			break;
		case 0x75:
			c = KeyEvent.VK_DELETE;
			break;
		case 0x76:
			c = KeyEvent.VK_F4;
			break;
		case 0x77:
			c = KeyEvent.VK_END;
			break;
		case 0x78:
			c = KeyEvent.VK_F2;
			break;
		case 0x79:
			c = KeyEvent.VK_PAGE_DOWN;
			break;
		case 0x7A:
			c = KeyEvent.VK_F1;
			break;
		case 0x7B:
			c = KeyEvent.VK_LEFT;
			break;
		case 0x7C:
			c = KeyEvent.VK_RIGHT;
			break;
		case 0x7D:
			c = KeyEvent.VK_DOWN;
			break;
		case 0x7E:
			c = KeyEvent.VK_UP;
			break;
		default:
			throw new InvalidKeyEventException("Unknown code '" + code + "' for OSX native key event.");
		}

		return NativeKeyEvent.of(KeyStroke.of(c, m, pressed, LocalDateTime.now()));
	}

	private boolean getPressedForFlags(int keyEventCode) throws InvalidKeyEventException {
		if (modifier == 256) {
			clearFlags();
			return false;
		}

		boolean x = toggleFlag(keyEventCode);
		return x;
	}

	private synchronized void checkProcessed() throws InvalidKeyEventException {
		if (processed) {
			throw new InvalidKeyEventException("An OSX native key event must not be converted twice.");
		}
		processed = true;
	}

	private static boolean toggleFlag(int keyEventCode) {
		boolean hasFlag;
		flagLock.lock();
		try {
			hasFlag = keyboardFlags.contains(keyEventCode);
			if (hasFlag) {
				keyboardFlags.remove(keyEventCode);
			} else {
				keyboardFlags.add(keyEventCode);
			}
		} finally {
			flagLock.unlock();
		}
		return !hasFlag;
	}

	private static void clearFlags() {
		flagLock.lock();
		try {
			keyboardFlags.clear();
		} finally {
			flagLock.unlock();
		}
	}
}
