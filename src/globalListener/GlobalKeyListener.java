package globalListener;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import utilities.Function;

public class GlobalKeyListener implements NativeKeyListener, GlobalListener {

	private static final long KEY_PRESS_DELAY_MS = 1000;

	private Function<NativeKeyEvent, Boolean> keyPressed;
	private Function<NativeKeyEvent, Boolean> keyReleased;
	private Map<Integer, Long> m;

	public GlobalKeyListener() {
		m = new HashMap<Integer, Long>();

		keyPressed = new Function<NativeKeyEvent, Boolean>() {
			@Override
			public Boolean apply(NativeKeyEvent r) {
				return true;
			}
		};

		keyReleased = new Function<NativeKeyEvent, Boolean>() {
			@Override
			public Boolean apply(NativeKeyEvent r) {
				return true;
			}
		};
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
				if (!keyPressed.apply(e)) {
					System.out.println("Internal key listener problem");
				}
			}
		}
		m.put(code, time);
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent e) {
		m.remove(e.getKeyCode());

		if (keyReleased != null) {
			if (!keyReleased.apply(e)) {
				System.out.println("Internal key listener problem");
			}
		}
	}

	@Override
	public final void nativeKeyTyped(NativeKeyEvent arg0) {
	}

	public void setKeyPressed(Function<NativeKeyEvent, Boolean> keyPressed) {
		this.keyPressed = keyPressed;
	}

	public void setKeyReleased(Function<NativeKeyEvent, Boolean> keyReleased) {
		this.keyReleased = keyReleased;
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

		GlobalKeyListener listener = new GlobalKeyListener();
		if (listener.startListening()) {
			GlobalScreen.addNativeKeyListener(listener);
		}
	}
}