package globalListener;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import utilities.Function;

public class GlobalKeyListener implements NativeKeyListener, GlobalListener {

	private Function<NativeKeyEvent, Boolean> keyPressed;
	private Function<NativeKeyEvent, Boolean> keyReleased;

	public GlobalKeyListener() {
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
		if (keyPressed != null) {
			if (!keyPressed.apply(e)) {
				System.out.println("Internal key listener problem");
			}
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent e) {
		if (keyReleased != null) {
			if (!keyReleased.apply(e)) {
				System.out.println("Internal key listener problem");
			}
		}
	}

	@Override
	public final void nativeKeyTyped(NativeKeyEvent arg0) {
	}

	public Function<NativeKeyEvent, Boolean> getKeyPressed() {
		return keyPressed;
	}

	public void setKeyPressed(Function<NativeKeyEvent, Boolean> keyPressed) {
		this.keyPressed = keyPressed;
	}

	public Function<NativeKeyEvent, Boolean> getKeyReleased() {
		return keyReleased;
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