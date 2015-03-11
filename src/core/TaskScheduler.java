package core;

import java.util.LinkedList;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import utilities.Function;
import core.Recorder.Task;

class TaskScheduler {

	private static final Logger LOGGER = Logger.getLogger(TaskScheduler.class.getName());

	static {
		LOGGER.setLevel(Level.ALL);
	}

	private final LinkedList<Task> tasks;
	private Thread executeAgent;
	private boolean isRunning;

	protected TaskScheduler() {
		tasks = new LinkedList<>();
	}

	protected synchronized boolean addTask(Task task) {
		if (isRunning) {
			LOGGER.warning("Failed attempt to add task to scheduler while running.");
			return false;
		}

		Stack<Task> temp = new Stack<>();
		while (true) {
			Task lastItem;
			if (tasks.isEmpty()) {
				lastItem = Task.EARLY_TASK;
			} else {
				lastItem = tasks.getLast();
			}

			if (lastItem.time < task.time) {
				tasks.addLast(task);
				while (!temp.isEmpty()) {
					tasks.add(temp.pop());
				}
				return true;
			} else {
				temp.push(tasks.removeLast());
			}
		}
	}

	protected Task getLast() {
		return tasks.getLast();
	}

	protected Task getFirst() {
		return tasks.getFirst();
	}

	protected synchronized long runTasks(final Function<Void, Void> callBack, final long callBackDelay) {
		if (isRunning) {
			LOGGER.info("Cannot invoke two running instances");
			return 0;
		}

		isRunning = true;
		Runnable running = new Runnable() {
			@Override
			public void run() {
				long time = 0;
				for (Task t : tasks) {
					long currentTime = t.time;

					if (currentTime < time) {
						LOGGER.severe("Something went really bad");
						System.exit(1);
					}

					try {
						Thread.sleep(currentTime - time);
					} catch (InterruptedException e) {
						LOGGER.info("Ended prematuredly");
						return; //Ended prematurely
					}

					time = currentTime;
					t.task.run();
				}

				if (callBack != null && callBackDelay > 0) {
					try {
						Thread.sleep(callBackDelay);
					} catch (InterruptedException e) {
						return; //Ended prematurely
					}
					callBack.apply(null);
				}
			}
		};
		executeAgent = new Thread(running);
		executeAgent.start();

		if (tasks.isEmpty()) {
			LOGGER.info("Nothing to run");
			return callBackDelay;
		} else {
			return tasks.getLast().time + callBackDelay;
		}
	}

	protected synchronized void halt() {
		if (isRunning) {
			if (Thread.currentThread() != executeAgent) {
				while (executeAgent.isAlive()) {
					executeAgent.interrupt();
				}
			}

			isRunning = false;
		} else {
			LOGGER.warning("Failed attempting to halt scheduler while not running!");
		}
	}

	protected synchronized boolean clearTasks() {
		if (isRunning) {
			LOGGER.info("Stop task scheduler first before clearing tasks");
			return false;
		}

		tasks.clear();
		return true;
	}
}
