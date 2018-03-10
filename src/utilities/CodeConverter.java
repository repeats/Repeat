package utilities;

import static org.jnativehook.keyboard.NativeKeyEvent.*;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import org.jnativehook.mouse.NativeMouseEvent;

import core.keyChain.KeyStroke;

public class CodeConverter {

	public static int getMouseButtonCode(int nativeCode, boolean isPressed) {
		if (isPressed) {
			if (nativeCode == NativeMouseEvent.BUTTON1_MASK) {
				return InputEvent.BUTTON1_DOWN_MASK;
			} else if (nativeCode == NativeMouseEvent.BUTTON2_MASK) {
				return InputEvent.BUTTON3_DOWN_MASK;
			} else if (nativeCode == NativeMouseEvent.BUTTON3_MASK) {
				return InputEvent.BUTTON2_DOWN_MASK;
			} else {
				return InputEvent.BUTTON1_DOWN_MASK;
			}
		} else {
			if (nativeCode == NativeMouseEvent.BUTTON1) {
				return InputEvent.BUTTON1_DOWN_MASK;
			} else if (nativeCode == NativeMouseEvent.BUTTON2) {
				return InputEvent.BUTTON3_DOWN_MASK;
			} else if (nativeCode == NativeMouseEvent.BUTTON3) {
				return InputEvent.BUTTON2_DOWN_MASK;
			} else {
				return InputEvent.BUTTON1_DOWN_MASK;
			}
		}
	}

	public static KeyStroke getKeyEventCode(int nativeCode) {
		int code = -1;
		KeyStroke.Modifier modifier = KeyStroke.Modifier.KEY_MODIFIER_UNKNOWN;
		// Lookup text values.

		switch (nativeCode) {

		case VC_ESCAPE:

			code = KeyEvent.VK_ESCAPE;
			break;

			// Begin Function Keys

		case VC_F1:

			code = KeyEvent.VK_F1;
			break;

		case VC_F2:

			code = KeyEvent.VK_F2;
			break;

		case VC_F3:

			code = KeyEvent.VK_F3;
			break;

		case VC_F4:

			code = KeyEvent.VK_F4;
			break;

		case VC_F5:

			code = KeyEvent.VK_F5;
			break;

		case VC_F6:

			code = KeyEvent.VK_F6;
			break;

		case VC_F7:

			code = KeyEvent.VK_F7;
			break;

		case VC_F8:

			code = KeyEvent.VK_F8;
			break;

		case VC_F9:

			code = KeyEvent.VK_F9;
			break;

		case VC_F10:

			code = KeyEvent.VK_F10;
			break;

		case VC_F11:

			code = KeyEvent.VK_F11;
			break;

		case VC_F12:

			code = KeyEvent.VK_F12;
			break;

		case VC_F13:

			code = KeyEvent.VK_F13;
			break;

		case VC_F14:

			code = KeyEvent.VK_F14;
			break;

		case VC_F15:

			code = KeyEvent.VK_F15;
			break;

		case VC_F16:

			code = KeyEvent.VK_F16;
			break;

		case VC_F17:

			code = KeyEvent.VK_F17;
			break;

		case VC_F18:

			code = KeyEvent.VK_F18;
			break;

		case VC_F19:

			code = KeyEvent.VK_F19;
			break;

		case VC_F20:

			code = KeyEvent.VK_F20;
			break;

		case VC_F21:

			code = KeyEvent.VK_F21;
			break;

		case VC_F22:

			code = KeyEvent.VK_F22;
			break;

		case VC_F23:

			code = KeyEvent.VK_F23;
			break;

		case VC_F24:

			code = KeyEvent.VK_F24;
			break;

			// End Function Keys

			// Begin Alphanumeric Zone

		case VC_BACKQUOTE:
			code = KeyEvent.VK_BACK_QUOTE;
			break;

		case VC_1:

			code = KeyEvent.VK_1;
			break;

		case VC_2:

			code = KeyEvent.VK_2;
			break;

		case VC_3:

			code = KeyEvent.VK_3;
			break;

		case VC_4:

			code = KeyEvent.VK_4;
			break;

		case VC_5:

			code = KeyEvent.VK_5;
			break;

		case VC_6:

			code = KeyEvent.VK_6;
			break;

		case VC_7:

			code = KeyEvent.VK_7;
			break;

		case VC_8:

			code = KeyEvent.VK_8;
			break;

		case VC_9:

			code = KeyEvent.VK_9;
			break;

		case VC_0:

			code = KeyEvent.VK_0;
			break;

		case VC_MINUS:

			code = KeyEvent.VK_MINUS;
			break;

		case VC_EQUALS:

			code = KeyEvent.VK_EQUALS;
			break;

		case VC_BACKSPACE:

			code = KeyEvent.VK_BACK_SPACE;
			break;

		case VC_TAB:

			code = KeyEvent.VK_TAB;
			break;

		case VC_CAPS_LOCK:

			code = KeyEvent.VK_CAPS_LOCK;
			break;

		case VC_A:

			code = KeyEvent.VK_A;
			break;

		case VC_B:

			code = KeyEvent.VK_B;
			break;

		case VC_C:

			code = KeyEvent.VK_C;
			break;

		case VC_D:

			code = KeyEvent.VK_D;
			break;

		case VC_E:

			code = KeyEvent.VK_E;
			break;

		case VC_F:

			code = KeyEvent.VK_F;
			break;

		case VC_G:

			code = KeyEvent.VK_G;
			break;

		case VC_H:

			code = KeyEvent.VK_H;
			break;

		case VC_I:

			code = KeyEvent.VK_I;
			break;

		case VC_J:

			code = KeyEvent.VK_J;
			break;

		case VC_K:

			code = KeyEvent.VK_K;
			break;

		case VC_L:

			code = KeyEvent.VK_L;
			break;

		case VC_M:

			code = KeyEvent.VK_M;
			break;

		case VC_N:

			code = KeyEvent.VK_N;
			break;

		case VC_O:

			code = KeyEvent.VK_O;
			break;

		case VC_P:

			code = KeyEvent.VK_P;
			break;

		case VC_Q:

			code = KeyEvent.VK_Q;
			break;

		case VC_R:

			code = KeyEvent.VK_R;
			break;

		case VC_S:

			code = KeyEvent.VK_S;
			break;

		case VC_T:

			code = KeyEvent.VK_T;
			break;

		case VC_U:

			code = KeyEvent.VK_U;
			break;

		case VC_V:

			code = KeyEvent.VK_V;
			break;

		case VC_W:

			code = KeyEvent.VK_W;
			break;

		case VC_X:

			code = KeyEvent.VK_X;
			break;

		case VC_Y:

			code = KeyEvent.VK_Y;
			break;

		case VC_Z:

			code = KeyEvent.VK_Z;
			break;

		case VC_OPEN_BRACKET:

			code = KeyEvent.VK_OPEN_BRACKET;
			break;

		case VC_CLOSE_BRACKET:

			code = KeyEvent.VK_CLOSE_BRACKET;
			break;

		case VC_BACK_SLASH:

			code = KeyEvent.VK_BACK_SLASH;
			break;

		case VC_SEMICOLON:

			code = KeyEvent.VK_SEMICOLON;
			break;

		case VC_QUOTE:

			code = KeyEvent.VK_QUOTE;
			break;

		case VC_ENTER:

			code = KeyEvent.VK_ENTER;
			break;

		case VC_COMMA:

			code = KeyEvent.VK_COMMA;
			break;

		case VC_PERIOD:

			code = KeyEvent.VK_PERIOD;
			break;

		case VC_SLASH:

			code = KeyEvent.VK_SLASH;
			break;

		case VC_SPACE:

			code = KeyEvent.VK_SPACE;
			break;

			// End Alphanumeric Zone

		case VC_PRINTSCREEN:

			code = KeyEvent.VK_PRINTSCREEN;
			break;

		case VC_SCROLL_LOCK:

			code = KeyEvent.VK_SCROLL_LOCK;
			break;

		case VC_PAUSE:

			code = KeyEvent.VK_PAUSE;
			break;

			// Begin Edit Key Zone

		case VC_INSERT:

			code = KeyEvent.VK_INSERT;
			break;

		case VC_DELETE:

			code = KeyEvent.VK_DELETE;
			break;

		case VC_HOME:

			code = KeyEvent.VK_HOME;
			break;

		case VC_END:

			code = KeyEvent.VK_END;
			break;

		case VC_PAGE_UP:

			code = KeyEvent.VK_PAGE_UP;
			break;

		case VC_PAGE_DOWN:

			code = KeyEvent.VK_PAGE_DOWN;
			break;

			// End Edit Key Zone

			// Begin Cursor Key Zone

		case VC_UP:

			code = KeyEvent.VK_UP;
			break;

		case VC_DOWN:

			code = KeyEvent.VK_DOWN;
			break;

		case VC_LEFT:

			code = KeyEvent.VK_LEFT;
			break;

		case VC_RIGHT:

			code = KeyEvent.VK_RIGHT;
			break;

			// End Cursor Key Zone

			// Begin Numeric Zone

		case VC_NUM_LOCK:

			code = KeyEvent.VK_NUM_LOCK;
			break;

		case VC_KP_DIVIDE:

			code = KeyEvent.VK_DIVIDE;
			break;

		case VC_KP_MULTIPLY:

			code = KeyEvent.VK_MULTIPLY;
			break;

		case VC_KP_SUBTRACT:

			code = KeyEvent.VK_SUBTRACT;
			break;

		case VC_KP_EQUALS:
			code = KeyEvent.VK_EQUALS;
			break;

		case VC_KP_ADD:

			code = KeyEvent.VK_ADD;
			break;

		case VC_KP_ENTER:

			code = KeyEvent.VK_ENTER;
			break;

		case VC_KP_SEPARATOR:

			code = KeyEvent.VK_SEPARATOR;
			break;

		case VC_KP_1:

			code = KeyEvent.VK_NUMPAD1;
			break;

		case VC_KP_2:

			code = KeyEvent.VK_NUMPAD2;
			break;

		case VC_KP_3:

			code = KeyEvent.VK_NUMPAD3;
			break;

		case VC_KP_4:

			code = KeyEvent.VK_NUMPAD4;
			break;

		case VC_KP_5:

			code = KeyEvent.VK_NUMPAD5;
			break;

		case VC_KP_6:

			code = KeyEvent.VK_NUMPAD6;
			break;

		case VC_KP_7:

			code = KeyEvent.VK_NUMPAD7;
			break;

		case VC_KP_8:

			code = KeyEvent.VK_NUMPAD8;
			break;

		case VC_KP_9:

			code = KeyEvent.VK_NUMPAD9;
			break;

		case VC_KP_0:

			code = KeyEvent.VK_NUMPAD0;
			break;

			// End Numeric Zone

			// Begin Modifier and Control Keys

		case VC_SHIFT_L:

			code = KeyEvent.VK_SHIFT;
			modifier = KeyStroke.Modifier.KEY_MODIFIER_LEFT;
			break;

		case VC_SHIFT_R:

			code = KeyEvent.VK_SHIFT;
			modifier = KeyStroke.Modifier.KEY_MODIFIER_RIGHT;
			break;

		case VC_CONTROL_L:

			code = KeyEvent.VK_CONTROL;
			modifier = KeyStroke.Modifier.KEY_MODIFIER_LEFT;
			break;

		case VC_CONTROL_R:

			code = KeyEvent.VK_CONTROL;
			modifier = KeyStroke.Modifier.KEY_MODIFIER_RIGHT;
			break;

		case VC_ALT_L:

			code = KeyEvent.VK_ALT;
			modifier = KeyStroke.Modifier.KEY_MODIFIER_LEFT;
			break;

		case VC_ALT_R:

			code = KeyEvent.VK_ALT;
			modifier = KeyStroke.Modifier.KEY_MODIFIER_RIGHT;
			break;

		case VC_META_L:
			if (OSIdentifier.IS_WINDOWS) {
				code = KeyEvent.VK_WINDOWS;
			} else {
				code = KeyEvent.VK_META;
			}
			modifier = KeyStroke.Modifier.KEY_MODIFIER_LEFT;
			break;

		case VC_META_R:
			if (OSIdentifier.IS_WINDOWS) {
				code = KeyEvent.VK_WINDOWS;
			} else {
				code = KeyEvent.VK_META;
			}
			modifier = KeyStroke.Modifier.KEY_MODIFIER_RIGHT;
			break;

		case VC_CONTEXT_MENU:

			code = KeyEvent.VK_CONTEXT_MENU;
			break;

			// End Modifier and Control Keys

		case VC_UNDEFINED:

			code = KeyEvent.VK_UNDEFINED;
			break;

		}

		return KeyStroke.of(code, modifier);
	}

}
