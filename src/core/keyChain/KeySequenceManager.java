package core.keyChain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import core.config.Config;
import core.userDefinedTask.UserDefinedAction;

public class KeySequenceManager extends KeyStrokeManager {

	private final RollingKeySeries currentRollingKeySeries;
	private final List<UserDefinedAction> registeredActions;

	public KeySequenceManager(Config config) {
		super(config);

		this.currentRollingKeySeries = new RollingKeySeries();
		this.registeredActions = new ArrayList<>();
	}

	@Override
	public void startListening() {
		// Do nothing.
	}

	@Override
	public Set<UserDefinedAction> onKeyStrokePressed(KeyStroke stroke) {
		if (!getConfig().isExecuteOnKeyReleased()) {
			currentRollingKeySeries.addKeyStroke(stroke);
			return considerTaskExecution(stroke);
		}

		return Collections.<UserDefinedAction>emptySet();
	}

	@Override
	public Set<UserDefinedAction> onKeyStrokeReleased(KeyStroke stroke) {
		if (getConfig().isExecuteOnKeyReleased()) {
			currentRollingKeySeries.addKeyStroke(stroke);
			return considerTaskExecution(stroke);
		}

		return Collections.<UserDefinedAction>emptySet();
	}

	@Override
	public void clear() {
		currentRollingKeySeries.clearKeys();
	}

	@Override
	public Set<UserDefinedAction> collision(Collection<TaskActivation> activations) {
		Set<UserDefinedAction> output = new HashSet<>();
		for (TaskActivation activation : activations) {
			for (UserDefinedAction action : registeredActions) {
				if (collisionWithAction(action, activation)) {
					output.add(action);
				}
			}
		}
		return output;
	}

	@Override
	public Set<UserDefinedAction> registerAction(UserDefinedAction action) {
		Set<UserDefinedAction> toRemove = collision(action.getActivation());
		toRemove.forEach(a -> unRegisterAction(a));

		registeredActions.add(action);
		return toRemove;
	}

	@Override
	public Set<UserDefinedAction> unRegisterAction(UserDefinedAction action) {
		Set<UserDefinedAction> output = new HashSet<>();
		for (Iterator<UserDefinedAction> iterator = registeredActions.iterator(); iterator.hasNext();) {
			UserDefinedAction existing = iterator.next();
			if (existing.equals(action)) {
				output.add(existing);
				iterator.remove();
			}
		}
		return output;
	}

	private boolean collisionWithAction(UserDefinedAction action, TaskActivation activation) {
		for (KeySequence sequence : activation.getKeySequences()) {
			for (KeySequence actionSequence : action.getActivation().getKeySequences()) {
				if (actionSequence.collideWith(sequence)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Given a new key stroke coming in, consider start executing actions based on their activations.
	 * @return set of actions to execute.
	 */
	private Set<UserDefinedAction> considerTaskExecution(KeyStroke key) {
		if (key.getKey() == Config.HALT_TASK && getConfig().isEnabledHaltingKeyPressed()) {
			clear();
			return null;
		}

		Set<UserDefinedAction> output = new HashSet<>();
		for (UserDefinedAction action : registeredActions) {
			TaskActivation activation = action.getActivation();
			for (KeySequence sequence : activation.getKeySequences()) {
				if (currentRollingKeySeries.collideWith(sequence)) {
					output.add(action);
				}
			}
		}

		return output;
	}
}
