package core.keyChain.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import core.config.Config;
import core.keyChain.ButtonStroke;
import core.keyChain.ButtonStroke.Source;
import core.keyChain.KeySequence;
import core.keyChain.RollingKeySeries;
import core.keyChain.TaskActivation;
import core.userDefinedTask.UserDefinedAction;

public class KeySequenceManager extends KeyStrokeManager {

	private final RollingKeySeries currentKeyboardRollingKeySeries;
	private final RollingKeySeries currentRollingKeySeries;
	private final List<UserDefinedAction> registeredActions;

	public KeySequenceManager(Config config) {
		super(config);

		this.currentKeyboardRollingKeySeries = new RollingKeySeries();
		this.currentRollingKeySeries = new RollingKeySeries();
		this.registeredActions = new ArrayList<>();
	}

	@Override
	public final void startListening() {
		// Do nothing.
	}

	@Override
	synchronized public Set<UserDefinedAction> onButtonStrokePressed(ButtonStroke stroke) {
		if (stroke.getSource() == Source.KEYBOARD) {
			currentKeyboardRollingKeySeries.addKeyStroke(stroke);
		}
		currentRollingKeySeries.addKeyStroke(stroke);
		if (!getConfig().isExecuteOnKeyReleased()) {
			return considerTaskExecution(stroke);
		}

		return Collections.<UserDefinedAction>emptySet();
	}

	@Override
	synchronized public Set<UserDefinedAction> onButtonStrokeReleased(ButtonStroke stroke) {
		if (getConfig().isExecuteOnKeyReleased()) {
			return considerTaskExecution(stroke);
		}

		return Collections.<UserDefinedAction>emptySet();
	}

	/**
	 * Given a new key stroke coming in, consider start executing actions based on their activations.
	 * @return set of actions to execute.
	 */
	private final Set<UserDefinedAction> considerTaskExecution(ButtonStroke key) {
		if (key.getKey() == Config.HALT_TASK && getConfig().isEnabledHaltingKeyPressed()) {
			clear();
			return Collections.<UserDefinedAction>emptySet();
		}

		if (key.getSource() == Source.KEYBOARD && key.equals(currentKeyboardRollingKeySeries.getLast())) {
			Set<UserDefinedAction> toExecute = tasksToExecute(currentKeyboardRollingKeySeries);
			if (!toExecute.isEmpty()) {
				return toExecute;
			}
		}

		if (key.equals(currentRollingKeySeries.getLast())) {
			return tasksToExecute(currentRollingKeySeries);
		}
		return Collections.<UserDefinedAction>emptySet();
	}

	private Set<UserDefinedAction> tasksToExecute(RollingKeySeries keySeries) {
		Set<UserDefinedAction> output = new HashSet<>();
		for (UserDefinedAction action : registeredActions) {
			TaskActivation activation = action.getActivation();
			for (KeySequence sequence : activation.getKeySequences()) {
				if (keySeries.collideWith(sequence)) {
					action.setInvoker(TaskActivation.newBuilder().withKeySequence(sequence.clone()).build());
					output.add(action);
				}
			}
		}

		return output;
	}

	@Override
	synchronized public final void clear() {
		currentKeyboardRollingKeySeries.clearKeys();
		currentRollingKeySeries.clearKeys();
	}

	@Override
	public final Set<UserDefinedAction> collision(Collection<TaskActivation> activations) {
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

	@Override
	public final Set<UserDefinedAction> registerAction(UserDefinedAction action) {
		Set<UserDefinedAction> toRemove = collision(action.getActivation());
		toRemove.forEach(a -> unRegisterAction(a));

		registeredActions.add(action);
		return toRemove;
	}

	@Override
	public final Set<UserDefinedAction> unRegisterAction(UserDefinedAction action) {
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
}
