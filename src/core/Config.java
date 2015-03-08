package core;

import java.awt.event.KeyEvent;

public final class Config {

	public static int RECORD = KeyEvent.VK_F12;
	public static int REPLAY = KeyEvent.VK_F11;
	public static int COMPILED_REPLAY = KeyEvent.VK_F10;

	public static boolean isSpecialKey(int code) {
		return (code == RECORD) || (code == REPLAY) || (code == COMPILED_REPLAY);
	}

	private Config() {}
}
