package core.ipc.repeatServer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TaskProcessorManager {

	private static final Map<String, TaskProcessor> taskManagers;

	static {
		taskManagers = Collections.synchronizedMap(new HashMap<String, TaskProcessor>());
	}

	public static boolean hasProcessor(String language) {
		return taskManagers.containsKey(language);
	}

	public static TaskProcessor getProcessor(String language) {
		return taskManagers.get(language);
	}

	public static void identifyProcessor(String language, TaskProcessor processor) {
		taskManagers.put(language, processor);
	}

	private TaskProcessorManager() {}
}
