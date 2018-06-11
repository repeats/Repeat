package core.keyChain.managers;

import java.util.HashSet;
import java.util.Set;

import core.config.Config;
import core.keyChain.ActivationPhrase;
import core.keyChain.TaskActivation;
import core.userDefinedTask.UserDefinedAction;

public class PhraseManager extends RollingKeySeriesManager {

	public PhraseManager(Config config) {
		super(config);
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
					action.setInvoker(TaskActivation.newBuilder().withPhrase(phrase.clone()).build());
					output.add(action);
				}
			}
		}

		return output;
	}
}
