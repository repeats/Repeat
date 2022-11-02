package globalListener;

import org.simplenativehooks.NativeHookInitializer;

public class GlobalListenerHookController {

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
		NativeHookInitializer.Config nativeConfig = NativeHookInitializer.Config.Builder.of().useJnaForWindows(true)
				.useJavaAwtToReportMousePositionOnWindows(config.useJavaAwtForMousePosition())
				.build();
		NativeHookInitializer.of(nativeConfig).start();
	}

	public void cleanup() {
		NativeHookInitializer.of().stop();
	}
}
