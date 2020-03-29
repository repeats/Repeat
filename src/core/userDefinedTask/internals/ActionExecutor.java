package core.userDefinedTask.internals;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import core.controller.CoreProvider;
import core.userDefinedTask.UserDefinedAction;
import utilities.RandomUtil;

public class ActionExecutor {

	private static final Logger LOGGER = Logger.getLogger(ActionExecutor.class.getName());

	private Map<String, Thread> executions;
	private CoreProvider coreProvider;

	public ActionExecutor(CoreProvider coreProvider) {
		this.coreProvider = coreProvider;
		this.executions = new HashMap<>();
	}

	/**
	 * Start executing actions, each in a separate thread.
	 *
	 * @param actions actions to execute.
	 */
	public void startExecutingActions(Collection<UserDefinedAction> actions) {
		for (UserDefinedAction action : actions) {
			startExecutingAction(action);
		}
	}


	private String startExecutingAction(UserDefinedAction action) {
		return startExecutingAction(ActionExecutionRequest.of(), action);
	}
	/**
	 * Start executing an action in a separate thread
	 *
	 * @param request request for execution of this action
	 * @param action action to execute
	 * @return ID of the registered execution
	 */
	public String startExecutingAction(ActionExecutionRequest request, UserDefinedAction action) {
		if (action == null) {
			return null;
		}
		if (request.getActivation() != null) {
			action.setInvoker(request.getActivation());
		}

		final String id = RandomUtil.randomID();
		Thread execution = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					for (int i = 0; i < request.getRepeatCount(); i++) {
						action.trackedAction(coreProvider.get());
						Thread.sleep(request.getDelayMsBetweenRepeat());
					}
				} catch (InterruptedException e) {
					LOGGER.info("Task ended prematurely");
				} catch (Exception e) {
					String name = action.getName() == null ? "" : action.getName();
					LOGGER.log(Level.WARNING, "Exception while executing task " + name, e);
				}

				executions.remove(id);
			}
		});

		executions.put(id, execution);
		execution.start();
		return id;
	}

	/**
	 * Interrupt all currently executing tasks, and clear the record of all executing tasks
	 */
	public void haltAllTasks() {
		LinkedList<Thread> endingThreads = new LinkedList<>();
		endingThreads.addAll(executions.values());

		for (Thread thread : endingThreads) {
			while (thread.isAlive() && thread != Thread.currentThread()) {
				LOGGER.info("Interrupting execution thread " + thread);
				thread.interrupt();
			}
		}
		executions.clear();
	}
}
