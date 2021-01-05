package core.keyChain.managers;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import core.config.Config;
import core.keyChain.ButtonStroke;
import core.keyChain.ButtonStroke.Source;
import core.keyChain.KeyChain;
import core.keyChain.TaskActivation;
import core.userDefinedTask.UserDefinedAction;

public class KeyChainManager extends KeyStrokeManager {

	private KeyChain currentKeyboardChain, currentKeyChain;
	private final Set<ButtonStroke> pressedKeyboardKeys, pressedKeys;
	private final Map<KeyChain, UserDefinedAction> keyChainActions;

	private UserDefinedAction pendingAction;

	public KeyChainManager(Config config) {
		super(config);
		currentKeyboardChain = new KeyChain();
		currentKeyChain = new KeyChain();
		pressedKeyboardKeys = Collections.synchronizedSet(new HashSet<>());
		pressedKeys = Collections.synchronizedSet(new HashSet<>());
		keyChainActions = new HashMap<>();

	}

	@Override
	public void startListening() {
		// Do nothing.
	}

	@Override
	public synchronized Set<UserDefinedAction> onButtonStrokePressed(ButtonStroke stroke) {
		if (stroke.getSource() == Source.KEYBOARD) {
			pressedKeyboardKeys.add(stroke);
		}
		pressedKeys.add(stroke);

		if (stroke.getSource() == Source.KEYBOARD) {
			currentKeyboardChain.addKeyStroke(stroke);
		}
		currentKeyChain.addKeyStroke(stroke);

		UserDefinedAction action = null;
		if (!getConfig().isExecuteOnKeyReleased()) {
			action = considerTaskExecution(stroke);
		}

		return Arrays.asList(action).stream().filter(a -> a != null).collect(Collectors.toSet());
	}

	@Override
	public synchronized Set<UserDefinedAction> onButtonStrokeReleased(ButtonStroke stroke) {
		if (stroke.getSource() == Source.KEYBOARD) {
			pressedKeyboardKeys.remove(stroke);
		}
		pressedKeys.remove(stroke);
		UserDefinedAction action = null;
		if (getConfig().isExecuteOnKeyReleased()) {
			action = considerTaskExecution(stroke);
		}

		if (stroke.getSource() == Source.KEYBOARD) {
			currentKeyboardChain.clearKeys();
		}
		currentKeyChain.clearKeys();

		if (action != null) {
			pendingAction = action;
		}
		if (pressedKeyboardKeys.isEmpty() || pressedKeys.isEmpty()) {
			UserDefinedAction toExecute = pendingAction;
			pendingAction = null;
			return Arrays.asList(toExecute).stream().filter(a -> a != null).collect(Collectors.toSet());
		}

		return Collections.emptySet();
	}

	@Override
	public void clear() {
		currentKeyboardChain.clearKeys();
		currentKeyChain.clearKeys();
	}

	@Override
	public Set<UserDefinedAction> collision(Collection<TaskActivation> activations) {
		Set<KeyChain> keyChains = activations.stream().map(a -> a.getHotkeys()).flatMap(Set::stream).collect(Collectors.toSet());

		Set<UserDefinedAction> collisions = new HashSet<>();
		for (Entry<KeyChain, UserDefinedAction> entry : keyChainActions.entrySet()) {
			KeyChain existing = entry.getKey();
			UserDefinedAction action = entry.getValue();

			for (KeyChain key : keyChains) {
				if (existing.collideWith(key)) {
					collisions.add(action);
				}
			}
		}
		return collisions;
	}

	@Override
	public Set<UserDefinedAction> registerAction(UserDefinedAction action) {
		Set<UserDefinedAction> toRemove = collision(action.getActivation());
		toRemove.forEach(a -> unRegisterAction(a));

		for (KeyChain key : action.getActivation().getHotkeys()) {
			keyChainActions.put(key, action);
		}

		return toRemove;
	}

	@Override
	public Set<UserDefinedAction> unRegisterAction(UserDefinedAction action) {
		return action.getActivation().getHotkeys().stream().map(k -> keyChainActions.remove(k)).filter(a -> a != null).collect(Collectors.toSet());
	}

	/**
	 * Given a new key code coming in, consider start executing an action based on its hotkey
	 * @param keyCode new keyCode coming in
	 * @return if operation succeeded (even if no action has been invoked)
	 */
	private UserDefinedAction considerTaskExecution(ButtonStroke stroke) {
		if (stroke.getKey() == Config.HALT_TASK && getConfig().isEnabledHaltingKeyPressed()) {
			clear();
			return null;
		}

		if (stroke.getSource() == Source.KEYBOARD) {
			UserDefinedAction action = keyChainActions.get(currentKeyboardChain);
			if (action != null) {
				action.setInvoker(TaskActivation.newBuilder().withHotKey(currentKeyboardChain.clone()).build());
			}
			return action;
		}

		UserDefinedAction action = keyChainActions.get(currentKeyChain);
		if (action != null) {
			action.setInvoker(TaskActivation.newBuilder().withHotKey(currentKeyChain.clone()).build());
		}

		return action;
	}
}
