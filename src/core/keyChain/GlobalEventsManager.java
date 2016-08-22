package core.keyChain;

import globalListener.GlobalKeyListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;

import utilities.CodeConverter;
import utilities.Function;
import utilities.RandomUtil;
import utilities.StringUtilities;
import core.config.Config;
import core.controller.Core;
import core.userDefinedTask.TaskGroup;
import core.userDefinedTask.UserDefinedAction;

public final class GlobalEventsManager {

	private static final Logger LOGGER = Logger.getLogger(GlobalEventsManager.class.getName());

	private final Config config;
	private final Map<KeyChain, UserDefinedAction> actionMap;
	/**
	 * This function is the precondition to executing any task.
	 * It is evaluated every time the manager considers executing any task.
	 * If it evaluates to true, the task will not be executed.
	 */
	private Function<Void, Boolean> disablingFunction;
	private final Map<String, Thread> executions;
	private final KeyChain currentKeyChain;
	private final MouseGestureManager mouseGestureManager;

	private TaskGroup currentTaskGroup;

	public GlobalEventsManager(Config config) {
		this.config = config;
		this.actionMap = new HashMap<>();
		this.executions = new HashMap<>();
		this.currentKeyChain = new KeyChain();
		this.disablingFunction = Function.falseFunction();
		this.mouseGestureManager = new MouseGestureManager();
	}

	public void startGlobalListener() throws NativeHookException {
		GlobalKeyListener keyListener = new GlobalKeyListener();
		keyListener.setKeyPressed(new Function<NativeKeyEvent, Boolean>() {
			@Override
			public Boolean apply(NativeKeyEvent r) {
				int code = CodeConverter.getKeyEventCode(r.getKeyCode());
				if (code == config.getMouseGestureActivationKey()) {
					mouseGestureManager.startRecoarding();
				}
				currentKeyChain.getKeys().add(code);

				if (!config.isExecuteOnKeyReleased()) {
					return considerTaskExecution(code);
				}
				return true;
			}
		});

		keyListener.setKeyReleased(new Function<NativeKeyEvent, Boolean>() {
			@Override
			public Boolean apply(NativeKeyEvent r) {
				int code = CodeConverter.getKeyEventCode(r.getKeyCode());
				if (code == config.getMouseGestureActivationKey()) {
					UserDefinedAction action =  mouseGestureManager.finishRecording();
					startExecutingAction(action);
				}

				if (config.isExecuteOnKeyReleased()) {
					boolean result = considerTaskExecution(code);
					currentKeyChain.getKeys().clear();
					return result;
				}
				currentKeyChain.getKeys().clear();
				return true;
			}
		});

		mouseGestureManager.startListening();
		keyListener.startListening();
	}

	public void setDisablingFunction(Function<Void, Boolean> disablingFunction) {
		this.disablingFunction = disablingFunction;
	}

	public void setCurrentTaskGroup(TaskGroup currentTaskGroup) {
		this.currentTaskGroup = currentTaskGroup;
	}

	/**
	 * Given a new key code coming in, consider start executing an action based on its hotkey
	 * @param keyCode new keyCode coming in
	 * @return if operation succeeded (even if no action has been invoked)
	 */
	private boolean considerTaskExecution(int keyCode) {
		if (keyCode == Config.HALT_TASK && config.isEnabledHaltingKeyPressed()) {
			currentKeyChain.getKeys().clear();
			haltAllTasks();
			return true;
		}

		if (disablingFunction.apply(null)) {
			return true;
		}

		UserDefinedAction action = actionMap.get(currentKeyChain);
		if (action != null) {
			action.setInvokingKeyChain(currentKeyChain.clone());
		}

		return startExecutingAction(action);
	}

	/**
	 * Start executing an action in a separate thread
	 *
	 * @param action action to execute
	 * @return if operation succeeded
	 */
	private boolean startExecutingAction(final UserDefinedAction action) {
		if (action == null) {
			return true;
		}

		final String id = RandomUtil.randomID();
		Thread execution = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					action.trackedAction(Core.getInstance());
				} catch (InterruptedException e) {
					LOGGER.info("Task ended prematurely");
				} catch (Exception e) {
					String name = action.getName() == null ? "" : action.getName();
					LOGGER.log(Level.WARNING, "Exception while executing task " + name, e);
				}

				executions.remove(id);
			}
		});

		executions.put(id, execution);
		execution.start();
		return true;
	}

	/**
	 * Map all key chains of the current task to the action. Kick out all colliding tasks.
	 * @param action action to register.
	 * @return List of currently registered tasks that collide with this newly registered task
	 */
	public Set<UserDefinedAction> registerTask(UserDefinedAction action) {
		Set<UserDefinedAction> collisions = isActivationRegistered(action.getActivation());
		Set<UserDefinedAction> output = new HashSet<>();

		for (UserDefinedAction toRemove : collisions) {
			output.add(toRemove);
		}

		for (KeyChain key : action.getActivation().getHotkeys()) {
			registerKey(key, action);
		}

		// Mouse gesture registration
		output.addAll(mouseGestureManager.registerAction(action));

		return output;
	}

	/**
	 * Unregister the action, then modify the action activation to be the new activation, and finally register the modified action.
	 * This kicks out all other actions that collide with the action provided.
	 *
	 * @param action action to be re-registered with new activation.
	 * @param newActivation new activation to be associated with the action.
	 * @return set of actions that collide with this action.
	 */
	public Set<UserDefinedAction> reRegisterTask(UserDefinedAction action, TaskActivation newActivation) {
		unregisterTask(action);
		action.getActivation().getHotkeys().clear();
		action.getActivation().getHotkeys().addAll(newActivation.getHotkeys());
		action.getActivation().getMouseGestures().clear();
		action.getActivation().getMouseGestures().addAll(newActivation.getMouseGestures());

		return registerTask(action);
	}

	/**
	 * Remove all bindings to the task's activation.
	 * @param action action whose activation will be removed.
	 * @return if all activations are removed.
	 */
	public boolean unregisterTask(UserDefinedAction action) {
		for (KeyChain k : action.getActivation().getHotkeys()) {
			unregisterKey(k);
		}

		mouseGestureManager.unRegisterAction(action);
		return true;
	}

	/**
	 * @param action
	 * @return return set of actions that collide with this action, excluding the input task.
	 */
	public Set<UserDefinedAction> isTaskRegistered(UserDefinedAction action) {
		Set<UserDefinedAction> output = isActivationRegistered(action.getActivation());
		output.remove(action);
		return output;
	}

	/**
	 * Check if an activation is already registered.
	 * @param activation
	 * @return return set of actions that collide with this activation.
	 */
	public Set<UserDefinedAction> isActivationRegistered(TaskActivation activation) {
		// Check for keychain collision
		Set<KeyChain> keyChainCollisions = new HashSet<>();
		for (KeyChain code : activation.getHotkeys()) {
			KeyChain collision = isKeyRegistered(code);
			if (collision != null) {
				keyChainCollisions.add(collision);
			}
		}

		// Check for mouse gesture collision
		Set<UserDefinedAction> gestureCollisions = mouseGestureManager.areGesturesRegistered(activation.getMouseGestures());

		Set<UserDefinedAction> output = new HashSet<>();
		for (KeyChain collision : keyChainCollisions) {
			output.add(actionMap.get(collision));
		}
		output.addAll(gestureCollisions);

		return output;
	}

	private KeyChain isKeyRegistered(KeyChain code) {
		for (KeyChain existing : actionMap.keySet()) {
			if (existing.collideWith(code)) {
				return existing;
			}
		}

		return null;
	}

	public KeyChain isKeyRegistered(int code) {
		return isKeyRegistered(new KeyChain(code));
	}

	/**
	 * Show a short notice that collision occurred.
	 *
	 * @param parent parent frame to show the notice in (null if there is none)
	 * @param collisions set of colliding tasks.
	 */
	public static void showCollisionWarning(JFrame parent, Set<UserDefinedAction> collisions) {
		String taskNames = StringUtilities.join(new Function<UserDefinedAction, String>() {
			@Override
			public String apply(UserDefinedAction d) {
				return '\'' + d.getName() + '\'';
			}}.map(collisions), ", ");

		JOptionPane.showMessageDialog(parent,
				"Newly registered keychains "
				+ "will collide with previously registered task(s) " + taskNames + "\n"
				+ "You cannot assign this key chain unless you remove the conflicting key chain...",
				"Key chain collision!", JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * Interrupt all currently executing tasks, and clear the record of all executing tasks
	 */
	public void haltAllTasks() {
		LinkedList<Thread> endingThreads = new LinkedList<>();
		endingThreads.addAll(executions.values());

		for (Thread thread : endingThreads) {
			while (thread.isAlive() && thread != Thread.currentThread()) {
				LOGGER.info("Interrupting execution thread " + thread);
				thread.interrupt();
			}
		}
		executions.clear();
	}

	private UserDefinedAction unregisterKey(KeyChain code) {
		return actionMap.remove(code);
	}

	private UserDefinedAction registerKey(KeyChain code, UserDefinedAction action) {
		if (code.getKeys().contains(Config.HALT_TASK)) {
			return null;
		}

		UserDefinedAction removal = actionMap.get(code);
		actionMap.put(code, action);

		return removal;
	}
}
