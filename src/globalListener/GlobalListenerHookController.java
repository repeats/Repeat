package globalListener;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.simplenativehooks.NativeHookInitializer;

public class GlobalListenerHookController {

	private static final Logger LOGGER = Logger.getLogger(GlobalListenerHookController.class.getName());
	private static final GlobalListenerHookController INSTANCE = new GlobalListenerHookController();

	private GlobalListenerHookController() {}

	public static GlobalListenerHookController of() {
		return INSTANCE;
	}

	public void initialize() {
		if (GlobalListenerFactory.USE_JNATIVE_HOOK) {
			// Get the logger for "org.jnativehook" and set the level to WARNING to begin with.
			Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
			logger.setLevel(Level.WARNING);

			if (!GlobalScreen.isNativeHookRegistered()) {
				try {
					GlobalScreen.registerNativeHook();
				} catch (NativeHookException e) {
					LOGGER.severe("Cannot register native hook!");
					System.exit(1);
				}
			}
		} else {
			NativeHookInitializer.of().start();
		}
	}

	public void cleanup() {
		if (GlobalListenerFactory.USE_JNATIVE_HOOK) {
			try {
				GlobalScreen.unregisterNativeHook();
			} catch (NativeHookException e) {
				LOGGER.log(Level.WARNING, "Unable to unregister JNative Hook.", e);
			}
		} else {
			NativeHookInitializer.of().stop();
		}
	}
}
