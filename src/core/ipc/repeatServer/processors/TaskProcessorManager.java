package core.ipc.repeatServer.processors;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import core.ipc.IPCServiceManager;
import core.languageHandler.Language;
import utilities.Function;

public final class TaskProcessorManager {

	private static final Map<Language, TaskProcessor> taskManagers;
	private static Function<Language, Void> callBack;

	static {
		taskManagers = Collections.synchronizedMap(new HashMap<Language, TaskProcessor>());
	}

	public static boolean hasProcessor(Language language) {
		return taskManagers.containsKey(language);
	}

	public static TaskProcessor getProcessor(Language language) {
		return taskManagers.get(language);
	}

	/**
	 * @return whether the client was successfully identified and associated to a language.
	 */
	public static boolean identifyProcessor(String language, int port, TaskProcessor processor) {
		final Language identified = Language.identify(language);
		if (identified == null || port <= 0) {
			return false;
		}
		getLogger().info("Identified remote compiler " + language);
		taskManagers.put(identified, processor);
		IPCServiceManager.getIPCService(identified).setPort(port);

		if (callBack != null) {
			// It is necessary to call back in a separate thread to not block the receiving
			// thread operation
			new Thread() {
				@Override
				public void run() {
					callBack.apply(identified);
				}
			}.start();
		}
		return true;
	}

	public static void setProcessorIdentifyCallback(Function<Language, Void> callBack) {
		TaskProcessorManager.callBack = callBack;
	}

	public static Logger getLogger() {
		return Logger.getLogger(TaskProcessorManager.class.getName());
	}

	private TaskProcessorManager() {}
}
