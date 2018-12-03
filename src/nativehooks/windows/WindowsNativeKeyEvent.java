package nativehooks.windows;

import java.awt.event.KeyEvent;
import java.time.LocalDateTime;

import core.keyChain.KeyStroke;
import core.keyChain.KeyStroke.Modifier;
import globalListener.NativeKeyEvent;
import nativehooks.NativeHookKeyEvent;
import nativehooks.UnknownKeyEventException;

class WindowsNativeKeyEvent extends NativeHookKeyEvent {
	private final int code;
	private final int param;

	private WindowsNativeKeyEvent(int code, int param) {
		this.code = code;
		this.param = param;
	}

	protected WindowsNativeKeyEvent of(int code, int param) {
		return new WindowsNativeKeyEvent(code, param);
	}

	@Override
	public NativeKeyEvent convertEvent() throws UnknownKeyEventException {
		boolean pressed = false;
		switch (param) {
		case 256:
			pressed = true;
			break;
		case 257:
			pressed = false;
			break;
		default:
			throw new UnknownKeyEventException("Unknown param " + param + ".");
		}

		return NativeKeyEvent.of(getKeyStroke(pressed));
	}

	private KeyStroke getKeyStroke(boolean pressed) {
		int c = KeyEvent.VK_UNDEFINED;
		Modifier modifier = Modifier.KEY_MODIFIER_UNKNOWN;

		switch (code) {
		case 0x08:
			c = KeyEvent.VK_BACK_SPACE;
			break;
		case 0x09:
			c = KeyEvent.VK_TAB;
			break;
		case 0x0C:
			c = KeyEvent.VK_CLEAR;
			break;
		case 0x0D:
			c = KeyEvent.VK_ENTER;
			break;
		case 0x10:
			c = KeyEvent.VK_SHIFT;
			break;
		case 0x11:
			c = KeyEvent.VK_CONTROL;
			break;
		case 0x12:
			c = KeyEvent.VK_ALT;
			break;
		case 0x13:
			c = KeyEvent.VK_PAUSE;
			break;
		case 0x14:
			c = KeyEvent.VK_CAPS_LOCK;
			break;
		case 0x18:
			c = KeyEvent.VK_FINAL;
			break;
		case 0x19:
			c = KeyEvent.VK_KANJI;
			break;
		case 0x1B:
			c = KeyEvent.VK_ESCAPE;
			break;
		case 0x1C:
			c = KeyEvent.VK_CONVERT;
			break;
		case 0x1D:
			c = KeyEvent.VK_NONCONVERT;
			break;
		case 0x1E:
			c = KeyEvent.VK_ACCEPT;
			break;
		case 0x1F:
			c = KeyEvent.VK_MODECHANGE;
			break;
		case 0x20:
			c = KeyEvent.VK_SPACE;
			break;
		case 0x21:
			c = KeyEvent.VK_PAGE_UP;
			break;
		case 0x22:
			c = KeyEvent.VK_PAGE_DOWN;
			break;
		case 0x23:
			c = KeyEvent.VK_END;
			break;
		case 0x24:
			c = KeyEvent.VK_HOME;
			break;
		case 0x25:
			c = KeyEvent.VK_LEFT;
			break;
		case 0x26:
			c = KeyEvent.VK_UP;
			break;
		case 0x27:
			c = KeyEvent.VK_RIGHT;
			break;
		case 0x28:
			c = KeyEvent.VK_DOWN;
			break;
		case 0x2D:
			c = KeyEvent.VK_INSERT;
			break;
		case 0x2E:
			c = KeyEvent.VK_DELETE;
			break;
		case 0x2F:
			c = KeyEvent.VK_HELP;
			break;
		case 0x30:
			c = KeyEvent.VK_0;
			break;
		case 0x31:
			c = KeyEvent.VK_1;
			break;
		case 0x32:
			c = KeyEvent.VK_2;
			break;
		case 0x33:
			c = KeyEvent.VK_3;
			break;
		case 0x34:
			c = KeyEvent.VK_4;
			break;
		case 0x35:
			c = KeyEvent.VK_5;
			break;
		case 0x36:
			c = KeyEvent.VK_6;
			break;
		case 0x37:
			c = KeyEvent.VK_7;
			break;
		case 0x38:
			c = KeyEvent.VK_8;
			break;
		case 0x39:
			c = KeyEvent.VK_9;
			break;
		case 0x41:
			c = KeyEvent.VK_A;
			break;
		case 0x42:
			c = KeyEvent.VK_B;
			break;
		case 0x43:
			c = KeyEvent.VK_C;
			break;
		case 0x44:
			c = KeyEvent.VK_D;
			break;
		case 0x45:
			c = KeyEvent.VK_E;
			break;
		case 0x46:
			c = KeyEvent.VK_F;
			break;
		case 0x47:
			c = KeyEvent.VK_G;
			break;
		case 0x48:
			c = KeyEvent.VK_H;
			break;
		case 0x49:
			c = KeyEvent.VK_I;
			break;
		case 0x4A:
			c = KeyEvent.VK_J;
			break;
		case 0x4B:
			c = KeyEvent.VK_K;
			break;
		case 0x4C:
			c = KeyEvent.VK_L;
			break;
		case 0x4D:
			c = KeyEvent.VK_M;
			break;
		case 0x4E:
			c = KeyEvent.VK_N;
			break;
		case 0x4F:
			c = KeyEvent.VK_O;
			break;
		case 0x50:
			c = KeyEvent.VK_P;
			break;
		case 0x51:
			c = KeyEvent.VK_Q;
			break;
		case 0x52:
			c = KeyEvent.VK_R;
			break;
		case 0x53:
			c = KeyEvent.VK_S;
			break;
		case 0x54:
			c = KeyEvent.VK_T;
			break;
		case 0x55:
			c = KeyEvent.VK_U;
			break;
		case 0x56:
			c = KeyEvent.VK_V;
			break;
		case 0x57:
			c = KeyEvent.VK_W;
			break;
		case 0x58:
			c = KeyEvent.VK_X;
			break;
		case 0x59:
			c = KeyEvent.VK_Y;
			break;
		case 0x5A:
			c = KeyEvent.VK_Z;
			break;
		case 0x5B:
			c = KeyEvent.VK_WINDOWS;
			modifier = Modifier.KEY_MODIFIER_LEFT;
			break;
		case 0x5C:
			c = KeyEvent.VK_WINDOWS;
			modifier = Modifier.KEY_MODIFIER_RIGHT;
			break;
		case 0x5D:
			c = KeyEvent.VK_CONTEXT_MENU;
			break;
		case 0x60:
			c = KeyEvent.VK_NUMPAD0;
			break;
		case 0x61:
			c = KeyEvent.VK_NUMPAD1;
			break;
		case 0x62:
			c = KeyEvent.VK_NUMPAD2;
			break;
		case 0x63:
			c = KeyEvent.VK_NUMPAD3;
			break;
		case 0x64:
			c = KeyEvent.VK_NUMPAD4;
			break;
		case 0x65:
			c = KeyEvent.VK_NUMPAD5;
			break;
		case 0x66:
			c = KeyEvent.VK_NUMPAD6;
			break;
		case 0x67:
			c = KeyEvent.VK_NUMPAD7;
			break;
		case 0x68:
			c = KeyEvent.VK_NUMPAD8;
			break;
		case 0x69:
			c = KeyEvent.VK_NUMPAD9;
			break;
		case 0x6A:
			c = KeyEvent.VK_MULTIPLY;
			break;
		case 0x6B:
			c = KeyEvent.VK_ADD;
			break;
		case 0x6C:
			c = KeyEvent.VK_SEPARATOR;
			break;
		case 0x6D:
			c = KeyEvent.VK_SUBTRACT;
			break;
		case 0x6E:
			c = KeyEvent.VK_DECIMAL;
			break;
		case 0x6F:
			c = KeyEvent.VK_DIVIDE;
			break;
		case 0x70:
			c = KeyEvent.VK_F1;
			break;
		case 0x71:
			c = KeyEvent.VK_F2;
			break;
		case 0x72:
			c = KeyEvent.VK_F3;
			break;
		case 0x73:
			c = KeyEvent.VK_F4;
			break;
		case 0x74:
			c = KeyEvent.VK_F5;
			break;
		case 0x75:
			c = KeyEvent.VK_F6;
			break;
		case 0x76:
			c = KeyEvent.VK_F7;
			break;
		case 0x77:
			c = KeyEvent.VK_F8;
			break;
		case 0x78:
			c = KeyEvent.VK_F9;
			break;
		case 0x79:
			c = KeyEvent.VK_F10;
			break;
		case 0x7A:
			c = KeyEvent.VK_F11;
			break;
		case 0x7B:
			c = KeyEvent.VK_F12;
			break;
		case 0x7C:
			c = KeyEvent.VK_F13;
			break;
		case 0x7D:
			c = KeyEvent.VK_F14;
			break;
		case 0x7E:
			c = KeyEvent.VK_F15;
			break;
		case 0x7F:
			c = KeyEvent.VK_F16;
			break;
		case 0x80:
			c = KeyEvent.VK_F17;
			break;
		case 0x81:
			c = KeyEvent.VK_F18;
			break;
		case 0x82:
			c = KeyEvent.VK_F19;
			break;
		case 0x83:
			c = KeyEvent.VK_F20;
			break;
		case 0x84:
			c = KeyEvent.VK_F21;
			break;
		case 0x85:
			c = KeyEvent.VK_F22;
			break;
		case 0x86:
			c = KeyEvent.VK_F23;
			break;
		case 0x87:
			c = KeyEvent.VK_F24;
			break;
		case 0x90:
			c = KeyEvent.VK_NUM_LOCK;
			break;
		case 0x91:
			c = KeyEvent.VK_SCROLL_LOCK;
			break;
		case 0xA0:
			c = KeyEvent.VK_SHIFT;
			modifier = Modifier.KEY_MODIFIER_LEFT;
			break;
		case 0xA1:
			c = KeyEvent.VK_SHIFT;
			modifier = Modifier.KEY_MODIFIER_RIGHT;
			break;
		case 0xA2:
			c = KeyEvent.VK_CONTROL;
			modifier = Modifier.KEY_MODIFIER_LEFT;
			break;
		case 0xA3:
			c = KeyEvent.VK_CONTROL;
			modifier = Modifier.KEY_MODIFIER_RIGHT;
			break;
		case 0xA4:
			c = KeyEvent.VK_CONTEXT_MENU;
			modifier = Modifier.KEY_MODIFIER_LEFT;
			break;
		case 0xA5:
			c = KeyEvent.VK_CONTEXT_MENU;
			modifier = Modifier.KEY_MODIFIER_RIGHT;
			break;
		case 0xBA:
			c = KeyEvent.VK_SEMICOLON;
			break;
		case 0xBB:
			c = KeyEvent.VK_PLUS;
			break;
		case 0xBC:
			c = KeyEvent.VK_COMMA;
			break;
		case 0xBD:
			c = KeyEvent.VK_MINUS;
			break;
		case 0xBE:
			c = KeyEvent.VK_PERIOD;
			break;
		case 0xBF:
			c = KeyEvent.VK_SLASH;
			break;
		case 0xC0:
			c = KeyEvent.VK_DEAD_TILDE;
			break;
		case 0xDB:
			c = KeyEvent.VK_BRACELEFT;
			break;
		case 0xDC:
			c = KeyEvent.VK_BACK_SLASH;
			break;
		case 0xDD:
			c = KeyEvent.VK_BRACERIGHT;
			break;
		case 0xDE:
			c = KeyEvent.VK_QUOTE;
			break;
		default:
			throw new UnknownKeyEventException("Unknown code " + code + " with param " + param + ".");
		}

		return KeyStroke.of(c, modifier, pressed, LocalDateTime.now());
	}
}
