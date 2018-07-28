package core.keyChain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jnativehook.keyboard.NativeKeyEvent;

import globalListener.GlobalKeyListener;
import utilities.Function;
import utilities.NativeHookCodeConverter;

public class TaskActivationConstructorManager {

	private static final long MAX_TIME_UNUSED_MS = 3600 * 1000;
	private static final long CLEAN_UP_PERIOD_SECOND = 1;

	private ScheduledThreadPoolExecutor executor;
	private GlobalKeyListener keyListener;

	private Map<String, TaskActivationConstructor> constructors;
	private Map<String, Long> lastUsed;

	public TaskActivationConstructorManager() {
		constructors = new HashMap<>();
		lastUsed = new HashMap<>();

		keyListener = new GlobalKeyListener();
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

		keyListener.setKeyReleased(new Function<NativeKeyEvent, Boolean>() {
			@Override
			public Boolean apply(NativeKeyEvent r) {
				KeyStroke stroke = NativeHookCodeConverter.getKeyEventCode(r.getKeyCode()).press(false).at(LocalDateTime.now());
				onStroke(stroke);
				return true;
			}
		});

		keyListener.startListening();
	}

	public void stop() {
		keyListener.stopListening();
		if (executor == null) {
			return;
		}
		executor.shutdown();
		executor = null;
	}

	public synchronized void onStroke(KeyStroke stroke) {
		for (TaskActivationConstructor constructor : constructors.values()) {
			constructor.onStroke(stroke);
		}
	}

	public synchronized String addNew(TaskActivation source) {
		return addNew(source, TaskActivationConstructor.Config.of());
	}

	public synchronized String addNew(TaskActivation source, TaskActivationConstructor.Config config) {
		String id = UUID.randomUUID().toString();
		TaskActivationConstructor constructor = new TaskActivationConstructor(source, config);

		constructors.put(id, constructor);
		lastUsed.put(id, System.currentTimeMillis());
		return id;
	}

	public synchronized TaskActivationConstructor getConstructor(String id) {
		TaskActivationConstructor output = constructors.get(id);
		lastUsed.put(id, System.currentTimeMillis());
		return output;
	}

	public synchronized void remove(String id) {
		lastUsed.remove(id);
		constructors.remove(id);
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
}
