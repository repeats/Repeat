package core.userDefinedTask;

import java.util.List;
import java.util.logging.Logger;

import core.controller.CoreProvider;
import core.keyChain.TaskActivation;

/**
 * Class to invoke tasks programmatically.
 */
public class TaskInvoker {

	private static final Logger LOGGER = Logger.getLogger(TaskInvoker.class.getName());

	private final CoreProvider coreProvider;
	private List<TaskGroup> taskGroup;

	public TaskInvoker(CoreProvider coreProvider, List<TaskGroup> taskGroup) {
		this.coreProvider = coreProvider;
		this.taskGroup = taskGroup;
	}

	/**
	 * Execute a task. Emit a warning and no-op if there is no such action.
	 *
	 * @param groupIndex
	 *            the index of the group that the task belongs to.
	 * @param taskIndex
	 *            the index of the task within the group.
	 * @throws InterruptedException
	 */
	public void execute(int groupIndex, int taskIndex) throws InterruptedException {
		execute(groupIndex, taskIndex, TaskActivation.newBuilder().build());
	}

	/**
	 * Execute a task. Emit a warning and no-op if there is no such task.
	 *
	 * @param groupIndex
	 *            the index of the group that the task belongs to.
	 * @param taskIndex
	 *            the index of the task within the group.
	 * @throws InterruptedException
	 */
	public void execute(int groupIndex, int taskIndex, TaskActivation activation) throws InterruptedException {
		if (groupIndex >= taskGroup.size()) {
			LOGGER.warning(String.format("Unable to execute task in group with index %d. There are only %d group(s).", groupIndex, taskGroup.size()));
			return;
		}
		TaskGroup group = taskGroup.get(groupIndex);

		if (taskIndex >= group.getTasks().size()) {
			LOGGER.warning(String.format("Unable to execute task in with index %d. Group %s only has %d tasks.", taskIndex, group.getName(), group.getTasks().size()));
			return;
		}
		UserDefinedAction task = group.getTasks().get(taskIndex);
		execute(task, activation);
	}

	/**
	 * Execute a task. Emit a warning and no-op if there is no such task.
	 *
	 * @param id
	 *            ID of the task.
	 * @throws InterruptedException
	 */
	public void execute(String id) throws InterruptedException {
		execute(id, TaskActivation.newBuilder().build());
	}

	/**
	 * Execute a task. Emit a warning and no-op if there is no such task.
	 *
	 * @param id
	 *            ID of the task.
	 * @param activation
	 *            task activation to associate with the execution.
	 * @throws InterruptedException
	 */
	public void execute(String id, TaskActivation activation) throws InterruptedException {
		for (TaskGroup group : taskGroup) {
			for (UserDefinedAction task : group.getTasks()) {
				if (task.getActionId().equals(id)) {
					execute(task, activation);
					return;
				}
			}
		}
		LOGGER.warning("Cannot find task with ID " + id + ".");
	}

	private void execute(UserDefinedAction action, TaskActivation activation) throws InterruptedException {
		action.setInvoker(activation);
		action.trackedAction(coreProvider.get());
	}
}
