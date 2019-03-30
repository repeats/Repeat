package globalListener;

import nativehooks.NativeKeyHook;
import nativehooks.NativeMouseHook;

public class GlobalListenerFactory {

	public static final boolean USE_JNATIVE_HOOK = false;
	public static final boolean USE_X11_ON_LINUX = true;

	private GlobalListenerFactory() {}

	public static GlobalListenerFactory of() {
		return new GlobalListenerFactory();
	}

	public AbstractGlobalKeyListener createGlobalKeyListener() {
		if (USE_JNATIVE_HOOK) {
			return new GlobalJNativeHookKeyListener();
		}

		return NativeKeyHook.of();
	}

	public AbstractGlobalMouseListener createGlobalMouseListener() {
		if (USE_JNATIVE_HOOK) {
			return new GlobalJNativeHookMouseListener();
		}

		return NativeMouseHook.of();
	}
}
