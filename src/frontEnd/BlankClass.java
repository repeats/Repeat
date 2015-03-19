package frontEnd;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import com.sun.glass.events.KeyEvent;


public class BlankClass {

	public static void main(String[] args) {
		// Get the logger for "org.jnativehook" and set the level to off.
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
		int a = KeyEvent.VK_0;
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
		GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
			@Override
			public void nativeKeyTyped(NativeKeyEvent arg0) {
				
			}
			
			@Override
			public void nativeKeyReleased(NativeKeyEvent arg0) {
				System.out.println(arg0.getKeyCode() + " ---> " + NativeKeyEvent.VC_SHIFT_L);
				
			}
			
			@Override
			public void nativeKeyPressed(NativeKeyEvent arg0) {
				System.out.println(arg0.getKeyCode());
			}
		});
	}
}
