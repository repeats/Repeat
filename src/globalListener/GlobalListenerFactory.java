package globalListener;

public class GlobalListenerFactory {

	private boolean useJNativeHook;

	private GlobalListenerFactory() {
		this.useJNativeHook = true;
	}

	public static GlobalListenerFactory of() {
		return new GlobalListenerFactory();
	}

	public AbstractGlobalKeyListener createGlobalKeyListener() {
		if (useJNativeHook) {
			return new GlobalJNativeHookKeyListener();
		}

		throw new IllegalStateException("Not sure what to use without JNativeHook...");
	}

	public AbstractGlobalMouseListener createGlobalMouseListener() {
		if (useJNativeHook) {
			return new GlobalJNativeHookMouseListener();
		}

		throw new IllegalStateException("Not sure what to use without JNativeHook...");
	}
}
