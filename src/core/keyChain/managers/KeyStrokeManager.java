package core.keyChain.managers;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import core.config.Config;
import core.keyChain.KeyStroke;
import core.keyChain.TaskActivation;
import core.userDefinedTask.UserDefinedAction;

public abstract class KeyStrokeManager {

	private Config config;

	public KeyStrokeManager(Config config) {
		this.config = config;
	}

	public Config getConfig() {
		return config;
	}

	public abstract void startListening();

	public abstract Set<UserDefinedAction> onKeyStrokePressed(KeyStroke stroke);
	public abstract Set<UserDefinedAction> onKeyStrokeReleased(KeyStroke stroke);

	public abstract void clear();

	public abstract Set<UserDefinedAction> collision(Collection<TaskActivation> activations);
	public final Set<UserDefinedAction> collision(TaskActivation activation) {
		return collision(Arrays.asList(activation));
	}

	public abstract Set<UserDefinedAction> registerAction(UserDefinedAction action);
	public abstract Set<UserDefinedAction> unRegisterAction(UserDefinedAction action);
}
