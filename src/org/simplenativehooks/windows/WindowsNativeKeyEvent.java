package org.simplenativehooks.windows;

import java.awt.event.KeyEvent;
import java.time.LocalDateTime;

import org.simplenativehooks.events.InvalidKeyEventException;
import org.simplenativehooks.events.NativeHookKeyEvent;
import org.simplenativehooks.events.NativeKeyEvent;

import core.keyChain.KeyStroke;
import core.keyChain.KeyStroke.Modifier;

class WindowsNativeKeyEvent extends NativeHookKeyEvent {
	private final int code;
	private final int param;

	private WindowsNativeKeyEvent(int code, int param) {
		this.code = code;
		this.param = param;
	}

	public static WindowsNativeKeyEvent of(int code, int param) {
		return new WindowsNativeKeyEvent(code, param);
	}

	@Override
	public NativeKeyEvent convertEvent() throws InvalidKeyEventException {
		boolean pressed = false;
		switch (param) {
		case 256:
			pressed = true;
			break;
		case 257:
			pressed = false;
			break;
		default:
			throw new InvalidKeyEventException("Unknown param '" + param + "'.");
		}

		return NativeKeyEvent.of(getKeyStroke(pressed));
	}

	private KeyStroke getKeyStroke(boolean pressed) throws InvalidKeyEventException {
		int k = KeyEvent.VK_UNDEFINED;
		Modifier m = Modifier.KEY_MODIFIER_UNKNOWN;

		switch (code) {
		case 0x08:
			k = KeyEvent.VK_BACK_SPACE;
			break;
		case 0x09:
			k = KeyEvent.VK_TAB;
			break;
		case 0x0C:
			k = KeyEvent.VK_CLEAR;
			break;
		case 0x0D:
			k = KeyEvent.VK_ENTER;
			break;
		case 0x10:
			k = KeyEvent.VK_SHIFT;
			break;
		case 0x11:
			k = KeyEvent.VK_CONTROL;
			break;
		case 0x12:
			k = KeyEvent.VK_ALT;
			break;
		case 0x13:
			k = KeyEvent.VK_PAUSE;
			break;
		case 0x14:
			k = KeyEvent.VK_CAPS_LOCK;
			break;
		case 0x18:
			k = KeyEvent.VK_FINAL;
			break;
		case 0x19:
			k = KeyEvent.VK_KANJI;
			break;
		case 0x1B:
			k = KeyEvent.VK_ESCAPE;
			break;
		case 0x1C:
			k = KeyEvent.VK_CONVERT;
			break;
		case 0x1D:
			k = KeyEvent.VK_NONCONVERT;
			break;
		case 0x1E:
			k = KeyEvent.VK_ACCEPT;
			break;
		case 0x1F:
			k = KeyEvent.VK_MODECHANGE;
			break;
		case 0x20:
			k = KeyEvent.VK_SPACE;
			break;
		case 0x21:
			k = KeyEvent.VK_PAGE_UP;
			break;
		case 0x22:
			k = KeyEvent.VK_PAGE_DOWN;
			break;
		case 0x23:
			k = KeyEvent.VK_END;
			break;
		case 0x24:
			k = KeyEvent.VK_HOME;
			break;
		case 0x25:
			k = KeyEvent.VK_LEFT;
			break;
		case 0x26:
			k = KeyEvent.VK_UP;
			break;
		case 0x27:
			k = KeyEvent.VK_RIGHT;
			break;
		case 0x28:
			k = KeyEvent.VK_DOWN;
			break;
		case 0x2D:
			k = KeyEvent.VK_INSERT;
			break;
		case 0x2E:
			k = KeyEvent.VK_DELETE;
			break;
		case 0x2F:
			k = KeyEvent.VK_HELP;
			break;
		case 0x30:
			k = KeyEvent.VK_0;
			break;
		case 0x31:
			k = KeyEvent.VK_1;
			break;
		case 0x32:
			k = KeyEvent.VK_2;
			break;
		case 0x33:
			k = KeyEvent.VK_3;
			break;
		case 0x34:
			k = KeyEvent.VK_4;
			break;
		case 0x35:
			k = KeyEvent.VK_5;
			break;
		case 0x36:
			k = KeyEvent.VK_6;
			break;
		case 0x37:
			k = KeyEvent.VK_7;
			break;
		case 0x38:
			k = KeyEvent.VK_8;
			break;
		case 0x39:
			k = KeyEvent.VK_9;
			break;
		case 0x41:
			k = KeyEvent.VK_A;
			break;
		case 0x42:
			k = KeyEvent.VK_B;
			break;
		case 0x43:
			k = KeyEvent.VK_C;
			break;
		case 0x44:
			k = KeyEvent.VK_D;
			break;
		case 0x45:
			k = KeyEvent.VK_E;
			break;
		case 0x46:
			k = KeyEvent.VK_F;
			break;
		case 0x47:
			k = KeyEvent.VK_G;
			break;
		case 0x48:
			k = KeyEvent.VK_H;
			break;
		case 0x49:
			k = KeyEvent.VK_I;
			break;
		case 0x4A:
			k = KeyEvent.VK_J;
			break;
		case 0x4B:
			k = KeyEvent.VK_K;
			break;
		case 0x4C:
			k = KeyEvent.VK_L;
			break;
		case 0x4D:
			k = KeyEvent.VK_M;
			break;
		case 0x4E:
			k = KeyEvent.VK_N;
			break;
		case 0x4F:
			k = KeyEvent.VK_O;
			break;
		case 0x50:
			k = KeyEvent.VK_P;
			break;
		case 0x51:
			k = KeyEvent.VK_Q;
			break;
		case 0x52:
			k = KeyEvent.VK_R;
			break;
		case 0x53:
			k = KeyEvent.VK_S;
			break;
		case 0x54:
			k = KeyEvent.VK_T;
			break;
		case 0x55:
			k = KeyEvent.VK_U;
			break;
		case 0x56:
			k = KeyEvent.VK_V;
			break;
		case 0x57:
			k = KeyEvent.VK_W;
			break;
		case 0x58:
			k = KeyEvent.VK_X;
			break;
		case 0x59:
			k = KeyEvent.VK_Y;
			break;
		case 0x5A:
			k = KeyEvent.VK_Z;
			break;
		case 0x5B:
			k = KeyEvent.VK_WINDOWS;
			m = Modifier.KEY_MODIFIER_LEFT;
			break;
		case 0x5C:
			k = KeyEvent.VK_WINDOWS;
			m = Modifier.KEY_MODIFIER_RIGHT;
			break;
		case 0x5D:
			k = KeyEvent.VK_CONTEXT_MENU;
			break;
		case 0x60:
			k = KeyEvent.VK_NUMPAD0;
			break;
		case 0x61:
			k = KeyEvent.VK_NUMPAD1;
			break;
		case 0x62:
			k = KeyEvent.VK_NUMPAD2;
			break;
		case 0x63:
			k = KeyEvent.VK_NUMPAD3;
			break;
		case 0x64:
			k = KeyEvent.VK_NUMPAD4;
			break;
		case 0x65:
			k = KeyEvent.VK_NUMPAD5;
			break;
		case 0x66:
			k = KeyEvent.VK_NUMPAD6;
			break;
		case 0x67:
			k = KeyEvent.VK_NUMPAD7;
			break;
		case 0x68:
			k = KeyEvent.VK_NUMPAD8;
			break;
		case 0x69:
			k = KeyEvent.VK_NUMPAD9;
			break;
		case 0x6A:
			k = KeyEvent.VK_MULTIPLY;
			break;
		case 0x6B:
			k = KeyEvent.VK_ADD;
			break;
		case 0x6C:
			k = KeyEvent.VK_SEPARATOR;
			break;
		case 0x6D:
			k = KeyEvent.VK_SUBTRACT;
			break;
		case 0x6E:
			k = KeyEvent.VK_DECIMAL;
			break;
		case 0x6F:
			k = KeyEvent.VK_DIVIDE;
			break;
		case 0x70:
			k = KeyEvent.VK_F1;
			break;
		case 0x71:
			k = KeyEvent.VK_F2;
			break;
		case 0x72:
			k = KeyEvent.VK_F3;
			break;
		case 0x73:
			k = KeyEvent.VK_F4;
			break;
		case 0x74:
			k = KeyEvent.VK_F5;
			break;
		case 0x75:
			k = KeyEvent.VK_F6;
			break;
		case 0x76:
			k = KeyEvent.VK_F7;
			break;
		case 0x77:
			k = KeyEvent.VK_F8;
			break;
		case 0x78:
			k = KeyEvent.VK_F9;
			break;
		case 0x79:
			k = KeyEvent.VK_F10;
			break;
		case 0x7A:
			k = KeyEvent.VK_F11;
			break;
		case 0x7B:
			k = KeyEvent.VK_F12;
			break;
		case 0x7C:
			k = KeyEvent.VK_F13;
			break;
		case 0x7D:
			k = KeyEvent.VK_F14;
			break;
		case 0x7E:
			k = KeyEvent.VK_F15;
			break;
		case 0x7F:
			k = KeyEvent.VK_F16;
			break;
		case 0x80:
			k = KeyEvent.VK_F17;
			break;
		case 0x81:
			k = KeyEvent.VK_F18;
			break;
		case 0x82:
			k = KeyEvent.VK_F19;
			break;
		case 0x83:
			k = KeyEvent.VK_F20;
			break;
		case 0x84:
			k = KeyEvent.VK_F21;
			break;
		case 0x85:
			k = KeyEvent.VK_F22;
			break;
		case 0x86:
			k = KeyEvent.VK_F23;
			break;
		case 0x87:
			k = KeyEvent.VK_F24;
			break;
		case 0x90:
			k = KeyEvent.VK_NUM_LOCK;
			break;
		case 0x91:
			k = KeyEvent.VK_SCROLL_LOCK;
			break;
		case 0xA0:
			k = KeyEvent.VK_SHIFT;
			m = Modifier.KEY_MODIFIER_LEFT;
			break;
		case 0xA1:
			k = KeyEvent.VK_SHIFT;
			m = Modifier.KEY_MODIFIER_RIGHT;
			break;
		case 0xA2:
			k = KeyEvent.VK_CONTROL;
			m = Modifier.KEY_MODIFIER_LEFT;
			break;
		case 0xA3:
			k = KeyEvent.VK_CONTROL;
			m = Modifier.KEY_MODIFIER_RIGHT;
			break;
		case 0xA4:
			k = KeyEvent.VK_ALT;
			m = Modifier.KEY_MODIFIER_LEFT;
			break;
		case 0xA5:
			k = KeyEvent.VK_ALT;
			m = Modifier.KEY_MODIFIER_RIGHT;
			break;
		case 0xBA:
			k = KeyEvent.VK_SEMICOLON;
			break;
		case 0xBB:
			k = KeyEvent.VK_PLUS;
			break;
		case 0xBC:
			k = KeyEvent.VK_COMMA;
			break;
		case 0xBD:
			k = KeyEvent.VK_MINUS;
			break;
		case 0xBE:
			k = KeyEvent.VK_PERIOD;
			break;
		case 0xBF:
			k = KeyEvent.VK_SLASH;
			break;
		case 0xC0:
			k = KeyEvent.VK_DEAD_TILDE;
			break;
		case 0xDB:
			k = KeyEvent.VK_BRACELEFT;
			break;
		case 0xDC:
			k = KeyEvent.VK_BACK_SLASH;
			break;
		case 0xDD:
			k = KeyEvent.VK_BRACERIGHT;
			break;
		case 0xDE:
			k = KeyEvent.VK_QUOTE;
			break;
		default:
			throw new InvalidKeyEventException("Unknown code '" + code + "' with param '" + param + "'.");
		}

		return KeyStroke.of(k, m, pressed, LocalDateTime.now());
	}
}
