package core.userDefinedTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import core.controller.Core;
import core.keyChain.TaskActivation;

public class AggregateUserDefinedAction extends UserDefinedAction {

	private static final Logger LOGGER = Logger.getLogger(AggregateUserDefinedAction.class.getName());

	private List<UserDefinedAction> actions;

	private AggregateUserDefinedAction(List<UserDefinedAction> actions) {
		UserDefinedAction reference = actions.iterator().next();
		syncContent(reference);
		this.actions = actions;
	}

	public static AggregateUserDefinedAction of(UserDefinedAction... actions) {
		return of(Arrays.asList(actions));
	}

	public static AggregateUserDefinedAction of(List<UserDefinedAction> actions) {
		if (actions.isEmpty()) {
			throw new RuntimeException("Empty list of actions.");
		}
		return new AggregateUserDefinedAction(actions);
	}

	@Override
	public final void action(Core controller) throws InterruptedException {
		List<Thread> executions = new ArrayList<>(actions.size());
		for (final UserDefinedAction action : actions) {
			executions.add(new Thread() {
				@Override
				public void run() {
					try {
						action.action(controller);
					} catch (InterruptedException ie) {
						LOGGER.log(Level.WARNING, "Interrupted when executing action.", ie);
					} catch (Exception e) {
						LOGGER.log(Level.WARNING, "Exception when executing action.", e);
					}
				}
			});
		}

		for (Thread t : executions) {
			t.start();
		}
		for (Thread t : executions) {
			t.join();
		}
	}

	@Override
	public final void setTaskInvoker(TaskInvoker taskInvoker) {
		for (UserDefinedAction action : actions) {
			action.setTaskInvoker(taskInvoker);
		}
	}

	@Override
	public final void setInvoker(TaskActivation invoker) {
		for (UserDefinedAction action : actions) {
			action.setInvoker(invoker);
		}
	}
}
