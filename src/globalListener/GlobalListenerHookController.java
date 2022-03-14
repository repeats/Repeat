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

	public static class Config {
		// Only applicable for Windows.
		private boolean useJavaAwtForMousePosition;

		private Config(boolean useJavaAwtForMousePosition) {
			this.useJavaAwtForMousePosition = useJavaAwtForMousePosition;
		}

		public boolean useJavaAwtForMousePosition() {
			return useJavaAwtForMousePosition;
		}

		public static class Builder {
			private boolean useJavaAwtForMousePosition;

			public static Builder of() {
				return new Builder();
			}

			public Builder useJavaAwtForMousePosition(boolean use) {
				this.useJavaAwtForMousePosition = use;
				return this;
			}

			public Config build() {
				return new Config(useJavaAwtForMousePosition);
			}
		}
	}

	public static GlobalListenerHookController of() {
		return INSTANCE;
	}

	public void initialize(Config config) {
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
			NativeHookInitializer.Config nativeConfig = NativeHookInitializer.Config.Builder.of().useJnaForWindows(true)
					.useJavaAwtToReportMousePositionOnWindows(config.useJavaAwtForMousePosition())
					.build();
			NativeHookInitializer.of(nativeConfig).start();
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
