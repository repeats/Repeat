package frontEnd;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;


public class BlankClass {

	public static void main(String[] args) {
		// Get the logger for "org.jnativehook" and set the level to off.
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);

		// Change the level for all handlers attached to the logger.
		Handler[] handlers = logger.getHandlers();
		for (int i = 0; i < handlers.length; i++) {
			handlers[i].setLevel(Level.OFF);
		}

		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException e) {
			e.printStackTrace();
			System.exit(0);
		}
		GlobalScreen.addNativeMouseListener(new NativeMouseListener() {

			@Override
			public void nativeMouseReleased(NativeMouseEvent arg0) {
				System.out.println(System.currentTimeMillis() + " --> " + arg0.getButton());
			}

			@Override
			public void nativeMousePressed(NativeMouseEvent arg0) {
				System.out.println(System.currentTimeMillis() + " --> " + arg0.getModifiers());
				int a = NativeMouseEvent.BUTTON1_MASK;
				int b = NativeMouseEvent.BUTTON2_MASK;
				int c = NativeMouseEvent.BUTTON3_MASK;
			}

			@Override
			public void nativeMouseClicked(NativeMouseEvent arg0) {

			}
		});
	}
}
