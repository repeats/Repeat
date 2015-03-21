package core;

import globalListener.GlobalKeyListener;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;

import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;

import utilities.CodeConverter;
import utilities.Function;
import core.config.Config;
import core.config.Parser1_0;
import core.controller.Core;

public final class GlobalKeysManager {

	private static final Logger LOGGER = Logger.getLogger(Parser1_0.class.getName());

	private final Core controller;
	private final Config config;
	private final Map<Integer, UserDefinedAction> actionMap = new HashMap<>();
	private Function<Void, Boolean> disablingFunction = Function.falseFunction();
	private final Map<String, Thread> executions;

	public GlobalKeysManager(Config config, Core controller) {
		this.controller = controller;
		this.executions = new HashMap<>();
		this.config = config;
	}

	public void startGlobalListener() throws NativeHookException {
		GlobalKeyListener keyListener = new GlobalKeyListener();
		keyListener.setKeyPressed(new Function<NativeKeyEvent, Boolean>() {
			@Override
			public Boolean apply(NativeKeyEvent r) {
				int code = CodeConverter.getKeyEventCode(r.getKeyCode());

				if (code == config.HALT_TASK) {
					LinkedList<Thread> endings = new LinkedList<>();
					for (Thread t : executions.values()) {
						endings.addLast(t);
					}

					for (Thread t : endings) {
						if (t.isAlive()) {
							t.interrupt();
						}
					}
					executions.clear();

					return true;
				}

				if (disablingFunction.apply(null)) {
					return true;
				}

				final UserDefinedAction action = actionMap.get(code);
				if (action != null) {
					final String id = System.currentTimeMillis() + ""; //Don't need the fancy UUID
					Thread execution = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								action.action(controller);
							} catch (InterruptedException e) {
								LOGGER.info("Task ended prematurely");
							}

							executions.remove(id);
						}
					});

					executions.put(id, execution);
					execution.start();

					return true;
				}

				return true;
			}
		});
		keyListener.startListening();
	}

	public void setDisablingFunction(Function<Void, Boolean> disablingFunction) {
		this.disablingFunction = disablingFunction;
	}

	public boolean isKeyRegistered(int code) {
		return actionMap.containsKey(code);
	}

	public UserDefinedAction unregisterKey(int code) {
		return actionMap.remove(code);
	}

	public UserDefinedAction registerKey(int code, UserDefinedAction action) {
		if (code == config.HALT_TASK) {
			return null;
		}

		UserDefinedAction removal = actionMap.get(code);
		actionMap.put(code, action);

		return removal;
	}

	public UserDefinedAction reRegisterKey(int code, int oldCode, UserDefinedAction action) {
		UserDefinedAction output = unregisterKey(oldCode);

		if (action == null && output != null) {
			registerKey(code, output);
		} else if (action != null) {
			registerKey(code, action);
		}

		return output;
	}
}
