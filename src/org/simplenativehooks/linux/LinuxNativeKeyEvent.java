package org.simplenativehooks.linux;

import java.awt.event.KeyEvent;
import java.time.LocalDateTime;

import org.simplenativehooks.InvalidKeyEventException;
import org.simplenativehooks.NativeHookKeyEvent;
import org.simplenativehooks.NativeKeyEvent;

import core.keyChain.KeyStroke;
import core.keyChain.KeyStroke.Modifier;

class LinuxNativeKeyEvent extends NativeHookKeyEvent {

	// See 'struct input_event' in linux/input.h.
	// Type is always assume to be EV_KEY (0x01).
	private int type;
	private int code;
	private int value;

	private LinuxNativeKeyEvent(int type, int code, int value) {
		this.type = type;
		this.code = code;
		this.value = value;
	}

	protected static LinuxNativeKeyEvent of(int type, int code, int value) {
		return new LinuxNativeKeyEvent(type, code, value);
	}

	@Override
	public NativeKeyEvent convertEvent() throws InvalidKeyEventException {
		if (type != 0x01) { // EV_KEY.
			throw new InvalidKeyEventException("Unknown key event with type " + type + ".");
		}

		boolean pressed;
		switch (value) {
		case 0:
			pressed = false;
			break;
		case 1:
			pressed = true;
			break;
		case 2: // When is is held down, resulting in a repeat.
			pressed = true;
			break;
		default:
			throw new InvalidKeyEventException("Unknown value '" + value + "'.");
		}

		return NativeKeyEvent.of(getKeyStroke(pressed));
	}

	private KeyStroke getKeyStroke(boolean pressed) throws InvalidKeyEventException {
		int c = KeyEvent.VK_UNDEFINED;
		Modifier m = Modifier.KEY_MODIFIER_UNKNOWN;

		switch (code) {
		case 1:
			c = KeyEvent.VK_ESCAPE;
			break;
		case 2:
			c = KeyEvent.VK_1;
			break;
		case 3:
			c = KeyEvent.VK_2;
			break;
		case 4:
			c = KeyEvent.VK_3;
			break;
		case 5:
			c = KeyEvent.VK_4;
			break;
		case 6:
			c = KeyEvent.VK_5;
			break;
		case 7:
			c = KeyEvent.VK_6;
			break;
		case 8:
			c = KeyEvent.VK_7;
			break;
		case 9:
			c = KeyEvent.VK_8;
			break;
		case 10:
			c = KeyEvent.VK_9;
			break;
		case 11:
			c = KeyEvent.VK_0;
			break;
		case 12:
			c = KeyEvent.VK_MINUS;
			break;
		case 13:
			c = KeyEvent.VK_EQUALS;
			break;
		case 14:
			c = KeyEvent.VK_BACK_SPACE;
			break;
		case 15:
			c = KeyEvent.VK_TAB;
			break;
		case 16:
			c = KeyEvent.VK_Q;
			break;
		case 17:
			c = KeyEvent.VK_W;
			break;
		case 18:
			c = KeyEvent.VK_E;
			break;
		case 19:
			c = KeyEvent.VK_R;
			break;
		case 20:
			c = KeyEvent.VK_T;
			break;
		case 21:
			c = KeyEvent.VK_Y;
			break;
		case 22:
			c = KeyEvent.VK_U;
			break;
		case 23:
			c = KeyEvent.VK_I;
			break;
		case 24:
			c = KeyEvent.VK_O;
			break;
		case 25:
			c = KeyEvent.VK_P;
			break;
		case 26:
			c = KeyEvent.VK_BRACELEFT;
			break;
		case 27:
			c = KeyEvent.VK_BRACERIGHT;
			break;
		case 28:
			c = KeyEvent.VK_ENTER;
			m = Modifier.KEY_MODIFIER_LEFT;
			break;
		case 29:
			c = KeyEvent.VK_CONTROL;
			m = Modifier.KEY_MODIFIER_LEFT;
			break;
		case 30:
			c = KeyEvent.VK_A;
			break;
		case 31:
			c = KeyEvent.VK_S;
			break;
		case 32:
			c = KeyEvent.VK_D;
			break;
		case 33:
			c = KeyEvent.VK_F;
			break;
		case 34:
			c = KeyEvent.VK_G;
			break;
		case 35:
			c = KeyEvent.VK_H;
			break;
		case 36:
			c = KeyEvent.VK_J;
			break;
		case 37:
			c = KeyEvent.VK_K;
			break;
		case 38:
			c = KeyEvent.VK_L;
			break;
		case 39:
			c = KeyEvent.VK_SEMICOLON;
			break;
		case 40:
			c = KeyEvent.VK_QUOTE;
			break;
		case 41:
			c = KeyEvent.VK_DEAD_TILDE;
			break;
		case 42:
			c = KeyEvent.VK_SHIFT;
			m = Modifier.KEY_MODIFIER_LEFT;
			break;
		case 43:
			c = KeyEvent.VK_BACK_SLASH;
			break;
		case 44:
			c = KeyEvent.VK_Z;
			break;
		case 45:
			c = KeyEvent.VK_X;
			break;
		case 46:
			c = KeyEvent.VK_C;
			break;
		case 47:
			c = KeyEvent.VK_V;
			break;
		case 48:
			c = KeyEvent.VK_B;
			break;
		case 49:
			c = KeyEvent.VK_N;
			break;
		case 50:
			c = KeyEvent.VK_M;
			break;
		case 51:
			c = KeyEvent.VK_COMMA;
			break;
		case 52:
			c = KeyEvent.VK_PERIOD;
			break;
		case 53:
			c = KeyEvent.VK_SLASH;
			break;
		case 54:
			c = KeyEvent.VK_SHIFT;
			m = Modifier.KEY_MODIFIER_RIGHT;
			break;
		case 55:
			c = KeyEvent.VK_MULTIPLY;
			break;
		case 56:
			c = KeyEvent.VK_ALT;
			m = Modifier.KEY_MODIFIER_LEFT;
			break;
		case 57:
			c = KeyEvent.VK_SPACE;
			break;
		case 58:
			c = KeyEvent.VK_CAPS_LOCK;
			break;
		case 59:
			c = KeyEvent.VK_F1;
			break;
		case 60:
			c = KeyEvent.VK_F2;
			break;
		case 61:
			c = KeyEvent.VK_F3;
			break;
		case 62:
			c = KeyEvent.VK_F4;
			break;
		case 63:
			c = KeyEvent.VK_F5;
			break;
		case 64:
			c = KeyEvent.VK_F6;
			break;
		case 65:
			c = KeyEvent.VK_F7;
			break;
		case 66:
			c = KeyEvent.VK_F8;
			break;
		case 67:
			c = KeyEvent.VK_F9;
			break;
		case 68:
			c = KeyEvent.VK_F10;
			break;
		case 69:
			c = KeyEvent.VK_NUM_LOCK;
			break;
		case 70:
			c = KeyEvent.VK_SCROLL_LOCK;
			break;
		case 71:
			c = KeyEvent.VK_NUMPAD7;
			break;
		case 72:
			c = KeyEvent.VK_NUMPAD8;
			break;
		case 73:
			c = KeyEvent.VK_NUMPAD9;
			break;
		case 74:
			c = KeyEvent.VK_SUBTRACT;
			break;
		case 75:
			c = KeyEvent.VK_NUMPAD4;
			break;
		case 76:
			c = KeyEvent.VK_NUMPAD5;
			break;
		case 77:
			c = KeyEvent.VK_NUMPAD6;
			break;
		case 78:
			c = KeyEvent.VK_PLUS;
			break;
		case 79:
			c = KeyEvent.VK_NUMPAD1;
			break;
		case 80:
			c = KeyEvent.VK_NUMPAD2;
			break;
		case 81:
			c = KeyEvent.VK_NUMPAD3;
			break;
		case 82:
			c = KeyEvent.VK_NUMPAD0;
			break;
		case 83:
			c = KeyEvent.VK_DECIMAL;
			break;
		case 87:
			c = KeyEvent.VK_F11;
			break;
		case 88:
			c = KeyEvent.VK_F12;
			break;
		case 96:
			c = KeyEvent.VK_ENTER;
			m = Modifier.KEY_MODIFIER_RIGHT;
			break;
		case 97:
			c = KeyEvent.VK_CONTROL;
			m = Modifier.KEY_MODIFIER_RIGHT;
			break;
		case 98:
			c = KeyEvent.VK_DIVIDE;
			break;
		case 99:
			c = KeyEvent.VK_PRINTSCREEN;
			break;
		case 100:
			c = KeyEvent.VK_ALT;
			m = Modifier.KEY_MODIFIER_RIGHT;
			break;
		case 102:
			c = KeyEvent.VK_HOME;
			break;
		case 103:
			c = KeyEvent.VK_UP;
			break;
		case 104:
			c = KeyEvent.VK_PAGE_UP;
			break;
		case 105:
			c = KeyEvent.VK_LEFT;
			break;
		case 106:
			c = KeyEvent.VK_RIGHT;
			break;
		case 107:
			c = KeyEvent.VK_END;
			break;
		case 108:
			c = KeyEvent.VK_DOWN;
			break;
		case 109:
			c = KeyEvent.VK_PAGE_DOWN;
			break;
		case 110:
			c = KeyEvent.VK_INSERT;
			break;
		case 111:
			c = KeyEvent.VK_DELETE;
			break;
		case 119:
			c = KeyEvent.VK_PAUSE;
			break;
		case 125:
			c = KeyEvent.VK_META;
			m = Modifier.KEY_MODIFIER_LEFT;
			break;
		case 126:
			c = KeyEvent.VK_META;
			m = Modifier.KEY_MODIFIER_RIGHT;
			break;
		case 127:
			c = KeyEvent.VK_COMPOSE;
			break;
		case 128:
			c = KeyEvent.VK_STOP;
			break;
		case 129:
			c = KeyEvent.VK_AGAIN;
			break;
		case 130:
			c = KeyEvent.VK_PROPS;
			break;
		case 131:
			c = KeyEvent.VK_UNDO;
			break;
		case 133:
			c = KeyEvent.VK_COPY;
			break;
		case 135:
			c = KeyEvent.VK_PASTE;
			break;
		case 136:
			c = KeyEvent.VK_FIND;
			break;
		case 137:
			c = KeyEvent.VK_CUT;
			break;
		case 138:
			c = KeyEvent.VK_HELP;
			break;
		case 139:
			c = KeyEvent.VK_CONTEXT_MENU;
			break;
		case 183:
			c = KeyEvent.VK_F13;
			break;
		case 184:
			c = KeyEvent.VK_F14;
			break;
		case 185:
			c = KeyEvent.VK_F15;
			break;
		case 186:
			c = KeyEvent.VK_F16;
			break;
		case 187:
			c = KeyEvent.VK_F17;
			break;
		case 188:
			c = KeyEvent.VK_F18;
			break;
		case 189:
			c = KeyEvent.VK_F19;
			break;
		case 190:
			c = KeyEvent.VK_F20;
			break;
		case 191:
			c = KeyEvent.VK_F21;
			break;
		case 192:
			c = KeyEvent.VK_F22;
			break;
		case 193:
			c = KeyEvent.VK_F23;
			break;
		case 194:
			c = KeyEvent.VK_F24;
			break;
		case 240: // This is KEY_UNKNOWN in linux/input.h.
			throw new InvalidKeyEventException("Encountered code KEY_UNKNOWN (" + code + ").");
		default:
			throw new InvalidKeyEventException("Unknown code '" + code + "'.");
		}

		return KeyStroke.of(c, m, pressed, LocalDateTime.now());
	}
}
