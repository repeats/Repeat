package core.keyChain.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import core.config.Config;
import core.keyChain.ActivationPhrase;
import core.keyChain.ButtonStroke;
import core.keyChain.ButtonStroke.Source;
import core.keyChain.RollingKeySeries;
import core.keyChain.TaskActivation;
import core.userDefinedTask.UserDefinedAction;

public class PhraseManager extends KeyStrokeManager {

	private final RollingKeySeries currentRollingKeySeries;
	private final List<UserDefinedAction> registeredActions;

	public PhraseManager(Config config) {
		super(config);

		this.currentRollingKeySeries = new RollingKeySeries();
		this.registeredActions = new ArrayList<>();
	}

	@Override
	public final void startListening() {
		// Do nothing.
	}

	@Override
	synchronized public Set<UserDefinedAction> onButtonStrokePressed(ButtonStroke stroke) {
		if (stroke.getSource() != Source.KEYBOARD) {
			return Collections.emptySet();
		}

		currentRollingKeySeries.addKeyStroke(stroke);
		if (!getConfig().isExecuteOnKeyReleased()) {
			return considerTaskExecution(stroke);
		}

		return Collections.<UserDefinedAction>emptySet();
	}

	@Override
	synchronized public Set<UserDefinedAction> onButtonStrokeReleased(ButtonStroke stroke) {
		if (stroke.getSource() != Source.KEYBOARD) {
			return Collections.emptySet();
		}
		currentRollingKeySeries.addKeyStroke(stroke);
		if (getConfig().isExecuteOnKeyReleased()) {
			return considerTaskExecution(stroke);
		}

		return Collections.<UserDefinedAction>emptySet();
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
		for (ActivationPhrase phrase : activation.getPhrases()) {
			for (ActivationPhrase actionPhrase : action.getActivation().getPhrases()) {
				if (phrase.collideWith(actionPhrase)) {
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

	@Override
	synchronized public final void clear() {
		currentRollingKeySeries.clearKeys();
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

		if (key.equals(currentRollingKeySeries.getLast())) {
			return tasksToExecute();
		}
		return Collections.<UserDefinedAction>emptySet();
	}

	private Set<UserDefinedAction> tasksToExecute() {
		Set<UserDefinedAction> output = new HashSet<>();
		for (UserDefinedAction action : registeredActions) {
			TaskActivation activation = action.getActivation();
			for (ActivationPhrase phrase : activation.getPhrases()) {
				if (currentRollingKeySeries.collideWith(phrase)) {
					action.setInvoker(TaskActivation.newBuilder().withPhrase(phrase.clone()).build());
					output.add(action);
				}
			}
		}

		return output;
	}
}
