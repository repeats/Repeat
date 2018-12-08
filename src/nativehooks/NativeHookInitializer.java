package nativehooks;

import java.util.logging.Level;
import java.util.logging.Logger;

import nativehooks.osx.GlobalOSXEventOchestrator;
import nativehooks.windows.GlobalWindowsEventOchestrator;
import utilities.OSIdentifier;

public class NativeHookInitializer {

	private static final Logger LOGGER = Logger.getLogger(NativeHookInitializer.class.getName());
	private static final NativeHookInitializer INSTANCE = new NativeHookInitializer();

	private NativeHookInitializer() {}

	public static NativeHookInitializer of() {
		return INSTANCE;
	}

	public void start() {
		if (OSIdentifier.IS_WINDOWS) {
			GlobalWindowsEventOchestrator.of().start();
			return;
		}
		if (OSIdentifier.IS_OSX) {
			GlobalOSXEventOchestrator.of().start();
			return;
		}

		throw new RuntimeException("OS not supported.");
	}

	public void stop() {
		if (OSIdentifier.IS_WINDOWS) {
			try {
				GlobalWindowsEventOchestrator.of().stop();
			} catch (InterruptedException e) {
				LOGGER.log(Level.WARNING, "Interrupted while stopping.", e);
			}
			return;
		}

		if (OSIdentifier.IS_OSX) {
			try {
				GlobalOSXEventOchestrator.of().stop();
			} catch (InterruptedException e) {
				LOGGER.log(Level.WARNING, "Interrupted while stopping.", e);
			}
			return;
		}

		throw new RuntimeException("OS not supported.");
	}
}
