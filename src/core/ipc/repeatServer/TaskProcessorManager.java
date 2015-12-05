package core.ipc.repeatServer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import core.languageHandler.Language;

public class TaskProcessorManager {

	private static final Map<Language, TaskProcessor> taskManagers;

	static {
		taskManagers = Collections.synchronizedMap(new HashMap<Language, TaskProcessor>());
	}

	public static boolean hasProcessor(String language) {
		return taskManagers.containsKey(language);
	}

	public static TaskProcessor getProcessor(Language language) {
		return taskManagers.get(language);
	}

	public static void identifyProcessor(String language, TaskProcessor processor) {
		Language identified = Language.identify(language);
		if (identified != null) {
			taskManagers.put(identified, processor);
		}
	}

	private TaskProcessorManager() {}
}
