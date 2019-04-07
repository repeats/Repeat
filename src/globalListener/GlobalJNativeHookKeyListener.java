package globalListener;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.simplenativehooks.listeners.AbstractGlobalKeyListener;

import core.keyChain.KeyStroke;
import utilities.JNativeHookCodeConverter;

/**
 * Implementation using JNativeHook as underlying library.
 */
public class GlobalJNativeHookKeyListener extends AbstractGlobalKeyListener implements NativeKeyListener {

	private static final Logger LOGGER = Logger.getLogger(GlobalJNativeHookKeyListener.class.getName());
	private static final long KEY_PRESS_DELAY_MS = 1000;

	private Map<Integer, Long> m;

	protected GlobalJNativeHookKeyListener() {
		super();
		m = new HashMap<Integer, Long>();
	}

	@Override
	public boolean startListening() {
		GlobalScreen.addNativeKeyListener(this);
		return true;
	}

	@Override
	public boolean stopListening() {
		GlobalScreen.removeNativeKeyListener(this);
		return true;
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		int code = e.getKeyCode();
		long time = System.currentTimeMillis();

		if (keyPressed != null) {
			if ((!m.containsKey(code)) || (m.get(code) - time >= KEY_PRESS_DELAY_MS)) {
				KeyStroke stroke = JNativeHookCodeConverter.getKeyEventCode(code).press(true).at(LocalDateTime.now());
				if (!keyPressed.apply(stroke.toNativeKeyEvent())) {
					LOGGER.warning("Internal key listener problem. Unable to apply key pressed action");
				}
			}
		}
		m.put(code, time);
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent e) {
		m.remove(e.getKeyCode());

		if (keyReleased != null) {
			KeyStroke stroke = JNativeHookCodeConverter.getKeyEventCode(e.getKeyCode()).press(false).at(LocalDateTime.now());

			if (!keyReleased.apply(stroke.toNativeKeyEvent())) {
				LOGGER.warning("Internal key listener problem. Unable to apply key released action");
			}
		}
	}

	@Override
	public final void nativeKeyTyped(NativeKeyEvent arg0) {
	}

	public static void main(String[] args) {
		// Get the logger for "org.jnativehook" and set the level to off.
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);

		// Change the level for all handlers attached to the default logger.
		Handler[] handlers = Logger.getLogger("").getHandlers();
		for (int i = 0; i < handlers.length; i++) {
			handlers[i].setLevel(Level.OFF);
		}

		GlobalJNativeHookKeyListener listener = new GlobalJNativeHookKeyListener();
		if (listener.startListening()) {
			GlobalScreen.addNativeKeyListener(listener);
		}
	}
}