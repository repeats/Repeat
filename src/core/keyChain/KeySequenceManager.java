package core.keyChain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import core.config.Config;
import core.userDefinedTask.UserDefinedAction;

public class KeySequenceManager extends RollingKeySeriesManager {

	public KeySequenceManager(Config config) {
		super(config);
	}

	@Override
	public Set<UserDefinedAction> onKeyStrokePressed(KeyStroke stroke) {
		currentRollingKeySeries.addKeyStroke(stroke);
		if (!getConfig().isExecuteOnKeyReleased()) {
			return considerTaskExecution(stroke);
		}

		return Collections.<UserDefinedAction>emptySet();
	}

	@Override
	public Set<UserDefinedAction> onKeyStrokeReleased(KeyStroke stroke) {
		if (getConfig().isExecuteOnKeyReleased()) {
			return considerTaskExecution(stroke);
		}

		return Collections.<UserDefinedAction>emptySet();
	}

	@Override
	protected boolean collisionWithAction(UserDefinedAction action, TaskActivation activation) {
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
	protected Set<UserDefinedAction> tasksToExecute() {
		Set<UserDefinedAction> output = new HashSet<>();
		for (UserDefinedAction action : registeredActions) {
			TaskActivation activation = action.getActivation();
			for (KeySequence sequence : activation.getKeySequences()) {
				if (currentRollingKeySeries.collideWith(sequence)) {
					action.setInvoker(TaskActivation.newBuilder().withKeySequence(sequence).build());
					output.add(action);
				}
			}
		}

		return output;
	}
}
