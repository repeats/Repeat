package core.recorder;


public class Task {
	protected static final Task EARLY_TASK = new Task(Long.MIN_VALUE, null);
	protected final long time;
	protected final Runnable task;

	protected Task(long time, Runnable task) {
		this.time = time;
		this.task = task;
	}
}
