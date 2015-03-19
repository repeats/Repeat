package core;

import globalListener.GlobalKeyListener;

import java.util.HashMap;
import java.util.Map;

import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;

import utilities.CodeConverter;
import utilities.Function;

public final class GlobalKeysManager {

	private static final Map<Integer, Function<Void, Void>> actionMap = new HashMap<>();
	private static Function<Void, Boolean> disablingFunction = Function.falseFunction();

	public static void startGlobalListener() throws NativeHookException {
		GlobalKeyListener keyListener = new GlobalKeyListener();
		keyListener.setKeyPressed(new Function<NativeKeyEvent, Boolean>() {
			@Override
			public Boolean apply(NativeKeyEvent r) {
				if (disablingFunction.apply(null)) {
					return false;
				}

				int code = CodeConverter.getKeyEventCode(r.getKeyCode());

				Function<Void, Void> action = actionMap.get(code);
				if (action != null) {
					action.apply(null);
					return true;
				}

				return false;
			}
		});
		keyListener.startListening();
	}

	public static void setDisablingFunction(Function<Void, Boolean> disablingFunction) {
		GlobalKeysManager.disablingFunction = disablingFunction;
	}

	public static boolean isKeyRegistered(int code) {
		return actionMap.containsKey(code);
	}

	public static Function<Void, Void> unregisterKey(int code) {
		return actionMap.remove(code);
	}

	public static Function<Void, Void> registerKey(int code, Function<Void, Void> action) {
		Function<Void, Void> removal = actionMap.get(code);
		actionMap.put(code, action);

		return removal;
	}

	private GlobalKeysManager() {}
}
