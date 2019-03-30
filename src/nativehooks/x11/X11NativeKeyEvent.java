package nativehooks.x11;

import java.awt.event.KeyEvent;
import java.time.LocalDateTime;

import core.keyChain.KeyStroke;
import core.keyChain.KeyStroke.Modifier;
import globalListener.NativeKeyEvent;
import nativehooks.InvalidKeyEventException;
import nativehooks.NativeHookKeyEvent;

public class X11NativeKeyEvent extends NativeHookKeyEvent {

	private String type;
	private int key;

	private X11NativeKeyEvent(String type, int key) {
		this.type = type;
		this.key = key;
	}

	public static X11NativeKeyEvent of(String type, int key) {
		return new X11NativeKeyEvent(type, key);
	}

	@Override
	public NativeKeyEvent convertEvent() throws InvalidKeyEventException {
		boolean pressed = false;
		switch (type) {
		case "P": // Pressed.
			pressed = true;
			break;
		case "E": // Repeated.
			pressed = true;
			break;
		case "R": // Released.
			pressed = false;
			break;
		default:
			throw new InvalidKeyEventException("Unknown key event with type '" + type + "'.");
		}

		Modifier m = Modifier.KEY_MODIFIER_UNKNOWN;
		int k = 1;
		switch (key) {
		case 1:
			k = KeyEvent.VK_ESCAPE;
			break;
		case 2:
			k = KeyEvent.VK_1;
			break;
		case 3:
			k = KeyEvent.VK_2;
			break;
		case 4:
			k = KeyEvent.VK_3;
			break;
		case 5:
			k = KeyEvent.VK_4;
			break;
		case 6:
			k = KeyEvent.VK_5;
			break;
		case 7:
			k = KeyEvent.VK_6;
			break;
		case 8:
			k = KeyEvent.VK_7;
			break;
		case 9:
			k = KeyEvent.VK_8;
			break;
		case 10:
			k = KeyEvent.VK_9;
			break;
		case 11:
			k = KeyEvent.VK_0;
			break;
		case 12:
			k = KeyEvent.VK_MINUS;
			break;
		case 13:
			k = KeyEvent.VK_EQUALS;
			break;
		case 14:
			k = KeyEvent.VK_BACK_SPACE;
			break;
		case 15:
			k = KeyEvent.VK_TAB;
			break;
		case 16:
			k = KeyEvent.VK_Q;
			break;
		case 17:
			k = KeyEvent.VK_W;
			break;
		case 18:
			k = KeyEvent.VK_E;
			break;
		case 19:
			k = KeyEvent.VK_R;
			break;
		case 20:
			k = KeyEvent.VK_T;
			break;
		case 21:
			k = KeyEvent.VK_Y;
			break;
		case 22:
			k = KeyEvent.VK_U;
			break;
		case 23:
			k = KeyEvent.VK_I;
			break;
		case 24:
			k = KeyEvent.VK_O;
			break;
		case 25:
			k = KeyEvent.VK_P;
			break;
		case 26:
			k = KeyEvent.VK_OPEN_BRACKET;
			break;
		case 27:
			k = KeyEvent.VK_CLOSE_BRACKET;
			break;
		case 28:
			k = KeyEvent.VK_ENTER;
			m = Modifier.KEY_MODIFIER_LEFT;
			break;
		case 29:
			k = KeyEvent.VK_CONTROL;
			m = Modifier.KEY_MODIFIER_RIGHT;
			break;
		case 30:
			k = KeyEvent.VK_A;
			break;
		case 31:
			k = KeyEvent.VK_S;
			break;
		case 32:
			k = KeyEvent.VK_D;
			break;
		case 33:
			k = KeyEvent.VK_F;
			break;
		case 34:
			k = KeyEvent.VK_G;
			break;
		case 35:
			k = KeyEvent.VK_H;
			break;
		case 36:
			k = KeyEvent.VK_J;
			break;
		case 37:
			k = KeyEvent.VK_K;
			break;
		case 38:
			k = KeyEvent.VK_L;
			break;
		case 39:
			k = KeyEvent.VK_SEMICOLON;
			break;
		case 40:
			k = KeyEvent.VK_QUOTE;
			break;
		case 41:
			k = KeyEvent.VK_DEAD_TILDE;
			break;
		case 42:
			k = KeyEvent.VK_SHIFT;
			m = Modifier.KEY_MODIFIER_RIGHT;
			break;
		case 43:
			k = KeyEvent.VK_BACK_SLASH;
			break;
		case 44:
			k = KeyEvent.VK_Z;
			break;
		case 45:
			k = KeyEvent.VK_X;
			break;
		case 46:
			k = KeyEvent.VK_C;
			break;
		case 47:
			k = KeyEvent.VK_V;
			break;
		case 48:
			k = KeyEvent.VK_B;
			break;
		case 49:
			k = KeyEvent.VK_N;
			break;
		case 50:
			k = KeyEvent.VK_M;
			break;
		case 51:
			k = KeyEvent.VK_COMMA;
			break;
		case 52:
			k = KeyEvent.VK_PERIOD;
			break;
		case 53:
			k = KeyEvent.VK_SLASH;
			break;
		case 54:
			k = KeyEvent.VK_SHIFT;
			m = Modifier.KEY_MODIFIER_RIGHT;
			break;
		case 55:
			k = KeyEvent.VK_MULTIPLY;
			break;
		case 56:
			k = KeyEvent.VK_ALT;
			m = Modifier.KEY_MODIFIER_LEFT;
			break;
		case 57:
			k = KeyEvent.VK_SPACE;
			break;
		case 58:
			k = KeyEvent.VK_CAPS_LOCK;
			break;
		case 59:
			k = KeyEvent.VK_F1;
			break;
		case 60:
			k = KeyEvent.VK_F2;
			break;
		case 61:
			k = KeyEvent.VK_F3;
			break;
		case 62:
			k = KeyEvent.VK_F4;
			break;
		case 63:
			k = KeyEvent.VK_F5;
			break;
		case 64:
			k = KeyEvent.VK_F6;
			break;
		case 65:
			k = KeyEvent.VK_F7;
			break;
		case 66:
			k = KeyEvent.VK_F8;
			break;
		case 67:
			k = KeyEvent.VK_F9;
			break;
		case 68:
			k = KeyEvent.VK_F10;
			break;
		case 69:
			k = KeyEvent.VK_NUM_LOCK;
			break;
		case 70:
			k = KeyEvent.VK_SCROLL_LOCK;
			break;
		case 71:
			k = KeyEvent.VK_NUMPAD7;
			break;
		case 72:
			k = KeyEvent.VK_NUMPAD8;
			break;
		case 73:
			k = KeyEvent.VK_NUMPAD9;
			break;
		case 74:
			k = KeyEvent.VK_SUBTRACT;
			break;
		case 75:
			k = KeyEvent.VK_NUMPAD4;
			break;
		case 76:
			k = KeyEvent.VK_NUMPAD5;
			break;
		case 77:
			k = KeyEvent.VK_NUMPAD6;
			break;
		case 78:
			k = KeyEvent.VK_ADD;
			break;
		case 79:
			k = KeyEvent.VK_NUMPAD1;
			break;
		case 80:
			k = KeyEvent.VK_NUMPAD2;
			break;
		case 81:
			k = KeyEvent.VK_NUMPAD3;
			break;
		case 82:
			k = KeyEvent.VK_NUMPAD0;
			break;
		case 83:
			k = KeyEvent.VK_DECIMAL;
			break;
		case 87:
			k = KeyEvent.VK_F11;
			break;
		case 88:
			k = KeyEvent.VK_F12;
			break;
		case 97:
			k = KeyEvent.VK_CONTROL;
			m = Modifier.KEY_MODIFIER_RIGHT;
			break;
		case 98:
			k = KeyEvent.VK_DIVIDE;
			break;
		case 99:
			k = KeyEvent.VK_PRINTSCREEN;
			break;
		case 100:
			k = KeyEvent.VK_ALT;
			m = Modifier.KEY_MODIFIER_RIGHT;
			break;
		case 102:
			k = KeyEvent.VK_HOME;
			break;
		case 103:
			k = KeyEvent.VK_UP;
			break;
		case 104:
			k = KeyEvent.VK_PAGE_UP;
			break;
		case 105:
			k = KeyEvent.VK_LEFT;
			break;
		case 106:
			k = KeyEvent.VK_RIGHT;
			break;
		case 107:
			k = KeyEvent.VK_END;
			break;
		case 108:
			k = KeyEvent.VK_DOWN;
			break;
		case 109:
			k = KeyEvent.VK_PAGE_DOWN;
			break;
		case 119:
			k = KeyEvent.VK_PAUSE;
			break;
		case 110:
			k = KeyEvent.VK_INSERT;
			break;
		case 111:
			k = KeyEvent.VK_DELETE;
			break;
		case 125:
			k = KeyEvent.VK_META;
			m = Modifier.KEY_MODIFIER_LEFT;
			break;
		case 127:
			k = KeyEvent.VK_CONTEXT_MENU;
			break;
		default:
			throw new InvalidKeyEventException("Uknown key code '" + key + "'.");
		}

		return NativeKeyEvent.of(KeyStroke.of(k, m, pressed, LocalDateTime.now()));
	}
}
