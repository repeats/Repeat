package core;

import globalListener.GlobalKeyListener;

import java.util.Collections;
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
	private final Map<KeyChain, UserDefinedAction> actionMap = new HashMap<>();
	private Function<Void, Boolean> disablingFunction = Function.falseFunction();
	private final Map<String, Thread> executions;
	private final KeyChain currentKeyChain;

	public GlobalKeysManager(Config config, Core controller) {
		this.controller = controller;
		this.executions = new HashMap<>();
		this.config = config;
		this.currentKeyChain = new KeyChain();
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
						while (t.isAlive()) {
							LOGGER.info("Interrupting execution thread " + t);
							t.interrupt();
						}
					}
					executions.clear();

					return true;
				}

				if (disablingFunction.apply(null)) {
					return true;
				}

				currentKeyChain.getKeys().add(code);

				final UserDefinedAction action = actionMap.get(currentKeyChain);
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

		keyListener.setKeyReleased(new Function<NativeKeyEvent, Boolean>() {
			@Override
			public Boolean apply(NativeKeyEvent r) {
				int code = CodeConverter.getKeyEventCode(r.getKeyCode());

				if (code == config.HALT_TASK) {
					currentKeyChain.getKeys().clear();
					return true;
				}

				currentKeyChain.getKeys().removeAll(Collections.singleton(Integer.valueOf(code)));
				return true;
			}
		});
		keyListener.startListening();
	}

	public void setDisablingFunction(Function<Void, Boolean> disablingFunction) {
		this.disablingFunction = disablingFunction;
	}

	public boolean isKeyRegistered(KeyChain code) {
		return actionMap.containsKey(code);
	}

	public boolean isKeyRegistered(int code) {
		return isKeyRegistered(new KeyChain(code));
	}

	public UserDefinedAction unregisterKey(KeyChain code) {
		return actionMap.remove(code);
	}

	public UserDefinedAction unregisterKey(int code) {
		return unregisterKey(new KeyChain(code));
	}

	public UserDefinedAction registerKey(KeyChain code, UserDefinedAction action) {
		if (code.getKeys().contains(config.HALT_TASK)) {
			return null;
		}

		UserDefinedAction removal = actionMap.get(code);
		actionMap.put(code, action);

		return removal;
	}

	public UserDefinedAction registerKey(int code, UserDefinedAction action) {
		return registerKey(new KeyChain(code), action);
	}

	public UserDefinedAction reRegisterKey(KeyChain code, KeyChain oldCode, UserDefinedAction action) {
		UserDefinedAction output = unregisterKey(oldCode);

		if (action == null && output != null) {
			registerKey(code, output);
		} else if (action != null) {
			registerKey(code, action);
		}

		return output;
	}

	public UserDefinedAction reRegisterKey(int code, int oldCode, UserDefinedAction action) {
		return reRegisterKey(new KeyChain(code),
							 new KeyChain(oldCode),
							 action);
	}
}
