package core.keyChain.managers;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import core.config.Config;
import core.keyChain.KeyChain;
import core.keyChain.KeyStroke;
import core.keyChain.TaskActivation;
import core.userDefinedTask.UserDefinedAction;

public class KeyChainManager extends KeyStrokeManager {

	private KeyChain currentKeyChain;
	private final Map<KeyChain, UserDefinedAction> keyChainActions;

	public KeyChainManager(Config config) {
		super(config);
		currentKeyChain = new KeyChain();
		keyChainActions = new HashMap<>();
	}

	@Override
	public void startListening() {
		// Do nothing.
	}

	@Override
	public Set<UserDefinedAction> onKeyStrokePressed(KeyStroke stroke) {
		currentKeyChain.addKeyStroke(stroke);

		UserDefinedAction action = null;
		if (!getConfig().isExecuteOnKeyReleased()) {
			action = considerTaskExecution(stroke.getKey());
		}

		return Arrays.asList(action).stream().filter(a -> a != null).collect(Collectors.toSet());
	}

	@Override
	public Set<UserDefinedAction> onKeyStrokeReleased(KeyStroke stroke) {
		UserDefinedAction action = null;
		if (getConfig().isExecuteOnKeyReleased()) {
			action = considerTaskExecution(stroke.getKey());
		}
		currentKeyChain.clearKeys();
		return Arrays.asList(action).stream().filter(a -> a != null).collect(Collectors.toSet());
	}

	@Override
	public void clear() {
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
	private UserDefinedAction considerTaskExecution(int keyCode) {
		if (keyCode == Config.HALT_TASK && getConfig().isEnabledHaltingKeyPressed()) {
			clear();
			return null;
		}

		UserDefinedAction action = keyChainActions.get(currentKeyChain);
		if (action != null) {
			action.setInvoker(TaskActivation.newBuilder().withHotKey(currentKeyChain.clone()).build());
		}

		return action;
	}
}
