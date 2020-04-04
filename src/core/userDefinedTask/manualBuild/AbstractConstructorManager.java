package core.userDefinedTask.manualBuild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class AbstractConstructorManager<T> {

	private static final long MAX_TIME_UNUSED_MS = 3600 * 1000;
	private static final long CLEAN_UP_PERIOD_SECOND = 1;

	protected Map<String, T> constructors;

	private ScheduledThreadPoolExecutor executor;
	private Map<String, Long> lastUsed;

	protected AbstractConstructorManager() {
		constructors = new HashMap<>();
		lastUsed = new HashMap<>();
	}

	protected final String addNew(T constructor) {
		String id = UUID.randomUUID().toString();

		constructors.put(id, constructor);
		lastUsed.put(id, System.currentTimeMillis());
		return id;
	}

	public void start() {
		if (executor == null) {
			executor = new ScheduledThreadPoolExecutor(2);
		}
		executor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				cleanup();
			}
		}, 0, CLEAN_UP_PERIOD_SECOND, TimeUnit.SECONDS);
	}

	public void stop() {
		if (executor == null) {
			return;
		}
		executor.shutdown();
		executor = null;
	}

	private synchronized void cleanup() {
		long now = System.currentTimeMillis();
		List<String> toRemove = new ArrayList<>();

		for (Iterator<Entry<String, Long>> iterator = lastUsed.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, Long> entry = iterator.next();
			String id = entry.getKey();
			Long lastUsed = entry.getValue();

			if (lastUsed - now > MAX_TIME_UNUSED_MS) {
				toRemove.add(id);
			}
		}

		toRemove.stream().forEach(this::remove);
	}


	public final T getConstructor(String id) {
		T output = constructors.get(id);
		lastUsed.put(id, System.currentTimeMillis());
		return output;
	}

	public synchronized void remove(String id) {
		lastUsed.remove(id);
		constructors.remove(id);
	}
}
