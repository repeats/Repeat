package core.keyChain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import core.config.Config;
import core.userDefinedTask.UserDefinedAction;

public class PhraseManager extends RollingKeySeriesManager {

	public PhraseManager(Config config) {
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
		currentRollingKeySeries.addKeyStroke(stroke);
		if (getConfig().isExecuteOnKeyReleased()) {
			return considerTaskExecution(stroke);
		}

		return Collections.<UserDefinedAction>emptySet();
	}

	@Override
	protected boolean collisionWithAction(UserDefinedAction action, TaskActivation activation) {
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
	protected Set<UserDefinedAction> tasksToExecute() {
		Set<UserDefinedAction> output = new HashSet<>();
		for (UserDefinedAction action : registeredActions) {
			TaskActivation activation = action.getActivation();
			for (ActivationPhrase phrase : activation.getPhrases()) {
				if (currentRollingKeySeries.collideWith(phrase)) {
					output.add(action);
				}
			}
		}

		return output;
	}
}
