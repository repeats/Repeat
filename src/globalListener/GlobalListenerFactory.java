package globalListener;

import org.simplenativehooks.NativeKeyHook;
import org.simplenativehooks.NativeMouseHook;
import org.simplenativehooks.listeners.AbstractGlobalKeyListener;
import org.simplenativehooks.listeners.AbstractGlobalMouseListener;

public class GlobalListenerFactory {

	private GlobalListenerFactory() {}

	public static GlobalListenerFactory of() {
		return new GlobalListenerFactory();
	}

	public AbstractGlobalKeyListener createGlobalKeyListener() {
		return NativeKeyHook.of();
	}

	public AbstractGlobalMouseListener createGlobalMouseListener() {
		return NativeMouseHook.of();
	}
}
