package core.keyChain.managers;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import core.config.Config;
import core.keyChain.KeyChain;
import core.keyChain.KeyStroke;
import core.keyChain.TaskActivation;
import core.userDefinedTask.UserDefinedAction;

public class GlobalKeyActionManager extends KeyStrokeManager {

	private Set<UserDefinedAction> onKeyStrokePressedTasks;
	private Set<UserDefinedAction> onKeyReleasedTasks;

	public GlobalKeyActionManager(Config config) {
		super(config);
		onKeyStrokePressedTasks = new HashSet<>();
		onKeyReleasedTasks = new HashSet<>();
	}

	@Override
	public void startListening() {
		// Nothing to do.
	}

	@Override
	public Set<UserDefinedAction> onKeyStrokePressed(KeyStroke stroke) {
		for (UserDefinedAction action : onKeyStrokePressedTasks) {
			action.setInvoker(TaskActivation.newBuilder().withHotKey(new KeyChain(Arrays.asList(stroke))).build());
		}

		return new HashSet<>(onKeyStrokePressedTasks);
	}

	@Override
	public Set<UserDefinedAction> onKeyStrokeReleased(KeyStroke stroke) {
		for (UserDefinedAction action : onKeyReleasedTasks) {
			action.setInvoker(TaskActivation.newBuilder().withHotKey(new KeyChain(Arrays.asList(stroke))).build());
		}

		return new HashSet<>(onKeyReleasedTasks);
	}

	@Override
	public void clear() {
		// Nothing to do.
	}

	@Override
	public Set<UserDefinedAction> collision(Collection<TaskActivation> activations) {
		return new HashSet<>();
	}

	@Override
	public Set<UserDefinedAction> registerAction(UserDefinedAction action) {
		if (action.getActivation().getGlobalActivation().isOnKeyPressed()) {
			onKeyStrokePressedTasks.add(action);
		}
		if (action.getActivation().getGlobalActivation().isOnKeyReleased()) {
			onKeyReleasedTasks.add(action);
		}
		return new HashSet<>();
	}

	@Override
	public Set<UserDefinedAction> unRegisterAction(UserDefinedAction action) {
		Set<UserDefinedAction> removed = new HashSet<>();
		for (Iterator<UserDefinedAction> it = onKeyStrokePressedTasks.iterator(); it.hasNext();) {
			UserDefinedAction pressed = it.next();
			if (pressed.equals(action)) {
				removed.add(pressed);
				it.remove();
			}
		}
		for (Iterator<UserDefinedAction> it = onKeyReleasedTasks.iterator(); it.hasNext();) {
			UserDefinedAction released = it.next();
			if (released.equals(action)) {
				removed.add(released);
				it.remove();
			}
		}
		return removed;
	}

}
