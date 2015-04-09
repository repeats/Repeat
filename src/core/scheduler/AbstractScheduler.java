package core.scheduler;

import java.util.LinkedList;
import java.util.Stack;

public abstract class AbstractScheduler<T> {
	protected final LinkedList<SchedulingData<T>> tasks;
	
	public AbstractScheduler() {
		this.tasks = new LinkedList<>();
	}
	
	public final synchronized boolean addTask(SchedulingData<T> task) {
		if (!isLegalAddTask()) {
			return false;
		}
		
		Stack<SchedulingData<T>> temp = new Stack<>();
		while (true) {
			SchedulingData<T> lastItem;
			if (tasks.isEmpty()) {
				lastItem = null;
			} else {
				lastItem = tasks.getLast();
			}

			if (lastItem == null || lastItem.getTime() < task.getTime()) {
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

	protected abstract boolean isLegalAddTask();
	
	protected SchedulingData<T> getLast() {
		return tasks.getLast();
	}

	protected SchedulingData<T> getFirst() {
		return tasks.getFirst();
	}
}
