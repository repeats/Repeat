package core;

import globalListener.GlobalKeyListener;
import globalListener.GlobalMouseListener;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.mouse.NativeMouseEvent;

import utilities.CodeConverter;
import utilities.Function;

public class Recorder {

	public static final int JAVA_LANGUAGE = 0;
	public static final int PYTHON_LANGUAGE = 1;

	public static final int MODE_NORMAL = 0;
	public static final int MODE_MOUSE_CLICK_ONLY = 1;

	private LinkedList<Task> tasks;
	private LinkedList<ScheduledFuture<?>> scheduled;
	private ScheduledThreadPoolExecutor executor;
	private long startTime;
	private int mode;

	private GlobalKeyListener keyListener;
	private GlobalMouseListener mouseListener;

	private HashMap<Integer, SourceGenerator> sourceGenerators;

	static {
		// Get the logger for "org.jnativehook" and set the level to off.
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);

		// Change the level for all handlers attached to the logger.
		Handler[] handlers = logger.getHandlers();
		for (int i = 0; i < handlers.length; i++) {
			handlers[i].setLevel(Level.OFF);
		}
	}

	public Recorder(final Core controller) {
		this.executor = new ScheduledThreadPoolExecutor(100);
		this.tasks = new LinkedList<>();
		this.scheduled = new LinkedList<>();

		sourceGenerators = new HashMap<>();
		sourceGenerators.put(JAVA_LANGUAGE, new JavaSourceGenerator());

		/*************************************************************************************************/
		keyListener = new GlobalKeyListener();
		keyListener.setKeyPressed(new Function<NativeKeyEvent, Boolean>() {
			@Override
			public Boolean apply(final NativeKeyEvent r) {
				final int code = CodeConverter.getKeyEventCode(r.getKeyCode());
				if (Config.isSpecialKey(code)) {
					return true;
				}

				final long time = System.currentTimeMillis() - startTime;
				tasks.add(new Task(time, new Runnable() {
					@Override
					public void run() {
						controller.keyBoard().press(code);
					}
				}));

				for (SourceGenerator generator : sourceGenerators.values()) {
					generator.submitTask(time, "keyBoard", "press", new int[]{code});
				}
				return true;
			}
		});

		keyListener.setKeyReleased(new Function<NativeKeyEvent, Boolean>() {
			@Override
			public Boolean apply(final NativeKeyEvent r) {
				final int code = CodeConverter.getKeyEventCode(r.getKeyCode());
				if (Config.isSpecialKey(code)) {
					return true;
				}

				final long time = System.currentTimeMillis() - startTime;
				tasks.add(new Task(time, new Runnable() {
					@Override
					public void run() {
						controller.keyBoard().release(code);
					}
				}));

				for (SourceGenerator generator : sourceGenerators.values()) {
					generator.submitTask(time, "keyBoard", "release", new int[]{code});
				}
				return true;
			}
		});

		/*************************************************************************************************/
		mouseListener = new GlobalMouseListener();
		mouseListener.setMouseReleased(new Function<NativeMouseEvent, Boolean>() {
			@Override
			public Boolean apply(final NativeMouseEvent r) {
				final int code = CodeConverter.getMouseButtonCode(r.getButton());
				final long time = System.currentTimeMillis() - startTime;
				tasks.add(new Task(time, new Runnable() {
					@Override
					public void run() {
						if (mode == MODE_MOUSE_CLICK_ONLY) {
							controller.mouse().move(r.getX(), r.getY());
						}
						controller.mouse().release(code);
					}
				}));

				for (SourceGenerator generator : sourceGenerators.values()) {
					if (mode == MODE_MOUSE_CLICK_ONLY) {
						generator.submitTask(time, "mouse", "move", new int[]{r.getX(), r.getY()});
						generator.submitTask(time + 5, "mouse", "release", new int[]{code});
					} else {
						generator.submitTask(time, "mouse", "release", new int[]{code});
					}
				}
				return true;
			}
		});

		mouseListener.setMousePressed(new Function<NativeMouseEvent, Boolean>() {
			@Override
			public Boolean apply(final NativeMouseEvent r) {
				final int code = CodeConverter.getMouseButtonCode(r.getButton());
				final long time = System.currentTimeMillis() - startTime;
				tasks.add(new Task(time, new Runnable() {
					@Override
					public void run() {
						if (mode == MODE_MOUSE_CLICK_ONLY) {
							controller.mouse().move(r.getX(), r.getY());
						}
						controller.mouse().press(code);
					}
				}));

				for (SourceGenerator generator : sourceGenerators.values()) {
					if (mode == MODE_MOUSE_CLICK_ONLY) {
						generator.submitTask(time, "mouse", "move", new int[]{r.getX(), r.getY()});
						generator.submitTask(time + 5, "mouse", "press", new int[]{code});
					} else {
						generator.submitTask(time, "mouse", "press", new int[]{code});
					}
				}
				return true;
			}
		});

		mouseListener.setMouseMoved(new Function<NativeMouseEvent, Boolean>() {
			@Override
			public Boolean apply(final NativeMouseEvent r) {
				if (mode == MODE_MOUSE_CLICK_ONLY) {
					return true;
				}

				final long time = System.currentTimeMillis() - startTime;
				tasks.add(new Task(time, new Runnable() {
					@Override
					public void run() {
						controller.mouse().move(r.getX(), r.getY());
					}
				}));

				for (SourceGenerator generator : sourceGenerators.values()) {
					generator.submitTask(time, "mouse", "move", new int[]{r.getX(), r.getY()});
				}
				return true;
			}
		});
	}

	public void setRecordMode(int mode) {
		this.mode = mode;
	}

	public void record() throws NativeHookException {
		if (!GlobalScreen.isNativeHookRegistered()) {
			GlobalScreen.registerNativeHook();
		}

		this.startTime = System.currentTimeMillis();
		this.keyListener.startListening();
		this.mouseListener.startListening();
	}

	public void stopRecord() throws NativeHookException {
		this.keyListener.stopListening();
		this.mouseListener.stopListening();

//		if (GlobalScreen.isNativeHookRegistered()) {
//			GlobalScreen.unregisterNativeHook();
//		}
	}

	public void replay() {
		replay(new Function<Void, Void>() {
			@Override
			public Void apply(Void r) {
				return null;
			}
		}, 0, true);
	}

	public void replay(final Function<Void, Void> callBack, long delayCallBack, boolean blocking) {
		long time = 0;
		for (Task t : tasks) {
			time = t.time;
			ScheduledFuture<?> future = executor.schedule(t.task, t.time, TimeUnit.MILLISECONDS);
			scheduled.add(future);
		}

		ScheduledFuture<?> lastCall = executor.schedule(new Runnable() {
			@Override
			public void run() {
				callBack.apply(null);
			}
		}, time + delayCallBack, TimeUnit.MILLISECONDS);
		scheduled.add(lastCall);

		if (blocking) {
			try {
				Thread.sleep(time + delayCallBack);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void stopReplay() {
		for (ScheduledFuture<?> f : scheduled) {
			f.cancel(false);
		}
		scheduled.clear();
	}

	public void clear() {
		for (SourceGenerator generator : sourceGenerators.values()) {
			generator.clear();
		}

		tasks.clear();
		scheduled.clear();
	}

	public String getGeneratedCode(int language) {
		SourceGenerator generator = sourceGenerators.get(language);
		if (generator != null) {
			return generator.getSource();
		} else {
			return null;
		}
	}

	private static class Task {
		private final long time;
		private final Runnable task;

		private Task(long time, Runnable task) {
			this.time = time;
			this.task = task;
		}
	}
}
