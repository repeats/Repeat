package utilities;

import java.awt.event.KeyEvent;

import core.keyChain.KeyboardState;

public class KeyCodeToChar {

	public static String getCharForCode(int code, KeyboardState state) {
		String nonAlphaChar = getNonAlphaChar(code, state);
		String alphaChar = getAlphaChar(code, state);
		String numpadChar = getCharFromNumpadCode(code, state);

		boolean isNonAlpha = !nonAlphaChar.isEmpty();
		boolean isAlpha= !alphaChar.isEmpty();
		boolean isNumpad = !numpadChar.isEmpty();

		int trueCount = (isNonAlpha ? 1 : 0) + (isAlpha ? 1 : 0) + (isNumpad ? 1 : 0);
		if (trueCount > 1) {
			throw new IllegalArgumentException("Code " + code + " is ambiguous.");
		}

		if (isNonAlpha) {
			return nonAlphaChar;
		}
		if (isAlpha) {
			return alphaChar;
		}
		if (isNumpad) {
			return numpadChar;
		}
		return "";
	}

	private static String getNonAlphaChar(int code, KeyboardState state) {
		if (state.isShiftLocked()) {
			return getNonAlphaCharWithShift(code);
		}
		return getNonAlphaCharWithoutShift(code);
	}

	private static String getAlphaChar(int code, KeyboardState state) {
		boolean capitalized = state.isCapslockLocked() ^ state.isShiftLocked();
		if (capitalized) {
			return getUpperCaseAlphaChar(code);
		}
		return getLowerCaseAlphaChar(code);
	}

	private static String getNonAlphaCharWithoutShift(int code) {
		switch (code) {
		case KeyEvent.VK_BACK_QUOTE:
			return "`";
		case KeyEvent.VK_1:
			return "1";
		case KeyEvent.VK_2:
			return "2";
		case KeyEvent.VK_3:
			return "3";
		case KeyEvent.VK_4:
			return "4";
		case KeyEvent.VK_5:
			return "5";
		case KeyEvent.VK_6:
			return "6";
		case KeyEvent.VK_7:
			return "7";
		case KeyEvent.VK_8:
			return "8";
		case KeyEvent.VK_9:
			return "9";
		case KeyEvent.VK_0:
			return "0";
		case KeyEvent.VK_MINUS:
			return "-";
		case KeyEvent.VK_EQUALS:
			return "=";
		case KeyEvent.VK_OPEN_BRACKET:
			return "[";
		case KeyEvent.VK_CLOSE_BRACKET:
			return "]";
		case KeyEvent.VK_SEMICOLON:
			return ";";
		case KeyEvent.VK_QUOTE:
			return "'";
		case KeyEvent.VK_BACK_SLASH:
			return "\\";
		case KeyEvent.VK_COMMA:
			return ",";
		case KeyEvent.VK_PERIOD:
			return ".";
		case KeyEvent.VK_SLASH:
			return "/";
		case KeyEvent.VK_TAB:
			return "\t";
		case KeyEvent.VK_ENTER:
			return "\n";
		case KeyEvent.VK_SPACE:
			return " ";
		default:
			return "";
		}
	}

	private static String getNonAlphaCharWithShift(int code) {
		switch (code) {
		case KeyEvent.VK_BACK_QUOTE:
			return "~";
		case KeyEvent.VK_1:
			return "!";
		case KeyEvent.VK_2:
			return "@";
		case KeyEvent.VK_3:
			return "#";
		case KeyEvent.VK_4:
			return "$";
		case KeyEvent.VK_5:
			return "%";
		case KeyEvent.VK_6:
			return "^";
		case KeyEvent.VK_7:
			return "&";
		case KeyEvent.VK_8:
			return "*";
		case KeyEvent.VK_9:
			return "(";
		case KeyEvent.VK_0:
			return ")";
		case KeyEvent.VK_MINUS:
			return "_";
		case KeyEvent.VK_EQUALS:
			return "+";
		case KeyEvent.VK_OPEN_BRACKET:
			return "{";
		case KeyEvent.VK_CLOSE_BRACKET:
			return "}";
		case KeyEvent.VK_SEMICOLON:
			return ":";
		case KeyEvent.VK_QUOTE:
			return "\"";
		case KeyEvent.VK_BACK_SLASH:
			return "|";
		case KeyEvent.VK_COMMA:
			return "<";
		case KeyEvent.VK_PERIOD:
			return ">";
		case KeyEvent.VK_SLASH:
			return "?";
		case KeyEvent.VK_TAB:
			return "\t";
		case KeyEvent.VK_ENTER:
			return "\n";
		case KeyEvent.VK_SPACE:
			return " ";
		default:
			return "";
		}
	}

	private static String getLowerCaseAlphaChar(int code) {
		switch (code) {
		case KeyEvent.VK_Q:
			return "q";
		case KeyEvent.VK_W:
			return "w";
		case KeyEvent.VK_E:
			return "e";
		case KeyEvent.VK_R:
			return "r";
		case KeyEvent.VK_T:
			return "t";
		case KeyEvent.VK_Y:
			return "y";
		case KeyEvent.VK_U:
			return "u";
		case KeyEvent.VK_I:
			return "i";
		case KeyEvent.VK_O:
			return "o";
		case KeyEvent.VK_P:
			return "p";
		case KeyEvent.VK_A:
			return "a";
		case KeyEvent.VK_S:
			return "s";
		case KeyEvent.VK_D:
			return "d";
		case KeyEvent.VK_F:
			return "f";
		case KeyEvent.VK_G:
			return "g";
		case KeyEvent.VK_H:
			return "h";
		case KeyEvent.VK_J:
			return "j";
		case KeyEvent.VK_K:
			return "k";
		case KeyEvent.VK_L:
			return "l";
		case KeyEvent.VK_Z:
			return "z";
		case KeyEvent.VK_X:
			return "x";
		case KeyEvent.VK_C:
			return "c";
		case KeyEvent.VK_V:
			return "v";
		case KeyEvent.VK_B:
			return "b";
		case KeyEvent.VK_N:
			return "n";
		case KeyEvent.VK_M:
			return "m";
		default:
			return "";
		}
	}

	private static String getUpperCaseAlphaChar(int code) {
		switch (code) {
		case KeyEvent.VK_Q:
			return "Q";
		case KeyEvent.VK_W:
			return "W";
		case KeyEvent.VK_E:
			return "E";
		case KeyEvent.VK_R:
			return "R";
		case KeyEvent.VK_T:
			return "T";
		case KeyEvent.VK_Y:
			return "Y";
		case KeyEvent.VK_U:
			return "U";
		case KeyEvent.VK_I:
			return "I";
		case KeyEvent.VK_O:
			return "O";
		case KeyEvent.VK_P:
			return "P";
		case KeyEvent.VK_A:
			return "A";
		case KeyEvent.VK_S:
			return "S";
		case KeyEvent.VK_D:
			return "D";
		case KeyEvent.VK_F:
			return "F";
		case KeyEvent.VK_G:
			return "G";
		case KeyEvent.VK_H:
			return "H";
		case KeyEvent.VK_J:
			return "J";
		case KeyEvent.VK_K:
			return "K";
		case KeyEvent.VK_L:
			return "L";
		case KeyEvent.VK_Z:
			return "Z";
		case KeyEvent.VK_X:
			return "X";
		case KeyEvent.VK_C:
			return "C";
		case KeyEvent.VK_V:
			return "V";
		case KeyEvent.VK_B:
			return "B";
		case KeyEvent.VK_N:
			return "N";
		case KeyEvent.VK_M:
			return "M";
		default:
			return "";
		}
	}

	private static String getCharFromNumpadCode(int code, KeyboardState state) {
		if (!state.isNumslockLocked()) {
			return "";
		}

		switch (code) {
			case KeyEvent.VK_NUMPAD0:
				return "0";
			case KeyEvent.VK_NUMPAD1:
				return "1";
			case KeyEvent.VK_NUMPAD2:
				return "2";
			case KeyEvent.VK_NUMPAD3:
				return "3";
			case KeyEvent.VK_NUMPAD4:
				return "4";
			case KeyEvent.VK_NUMPAD5:
				return "5";
			case KeyEvent.VK_NUMPAD6:
				return "6";
			case KeyEvent.VK_NUMPAD7:
				return "7";
			case KeyEvent.VK_NUMPAD8:
				return "8";
			case KeyEvent.VK_NUMPAD9:
				return "9";
			case KeyEvent.VK_PLUS:
				return "+";
			case KeyEvent.VK_MULTIPLY:
				return "*";
			default:
				return "";
		}
	}

	private KeyCodeToChar() {}
}
