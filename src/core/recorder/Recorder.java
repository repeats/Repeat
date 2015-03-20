package core.recorder;

import globalListener.GlobalKeyListener;
import globalListener.GlobalMouseListener;

import java.util.HashMap;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.mouse.NativeMouseEvent;

import utilities.CodeConverter;
import utilities.Function;
import core.GlobalKeysManager;
import core.controller.Core;
import core.languageHandler.JavaSourceGenerator;
import core.languageHandler.SourceGenerator;

public class Recorder {

	public static final int JAVA_LANGUAGE = 0;
	public static final int PYTHON_LANGUAGE = 1;

	public static final int MODE_NORMAL = 0;
	public static final int MODE_MOUSE_CLICK_ONLY = 1;

	private long startTime;
	private int mode;

	private TaskScheduler taskScheduler;

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

	public Recorder(final Core controller, final GlobalKeysManager globalKeys) {
		taskScheduler = new TaskScheduler();

		sourceGenerators = new HashMap<>();
		sourceGenerators.put(JAVA_LANGUAGE, new JavaSourceGenerator());

		/*************************************************************************************************/
		keyListener = new GlobalKeyListener();
		keyListener.setKeyPressed(new Function<NativeKeyEvent, Boolean>() {
			@Override
			public Boolean apply(final NativeKeyEvent r) {
				final int code = CodeConverter.getKeyEventCode(r.getKeyCode());
				if (globalKeys.isKeyRegistered(code)) {
					return true;
				}

				final long time = System.currentTimeMillis() - startTime;
				taskScheduler.addTask(new Task(time, new Runnable() {
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
				if (globalKeys.isKeyRegistered(code)) {
					return true;
				}

				final long time = System.currentTimeMillis() - startTime;
				taskScheduler.addTask(new Task(time, new Runnable() {
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
				final int code = CodeConverter.getMouseButtonCode(r.getButton(), false);
				final long time = System.currentTimeMillis() - startTime;
				taskScheduler.addTask(new Task(time + 20, new Runnable() {
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
				final int code = CodeConverter.getMouseButtonCode(r.getModifiers(), true);
				final long time = System.currentTimeMillis() - startTime;
				taskScheduler.addTask(new Task(time, new Runnable() {
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
				taskScheduler.addTask(new Task(time, new Runnable() {
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

	public void record() {
		this.startTime = System.currentTimeMillis();
		this.keyListener.startListening();
		this.mouseListener.startListening();
	}

	public void stopRecord() {
		this.keyListener.stopListening();
		this.mouseListener.stopListening();
	}

	public void replay() {
		replay(1, 0, null, 0, true);
	}

	public void replay(long count, long delay, Function<Void, Void> callBack, long callBackDelay, boolean blocking) {
		long time = taskScheduler.runTasks(count, delay, callBack, callBackDelay);

		if (blocking && time > 0) {
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void stopReplay() {
		taskScheduler.halt();
	}

	public void clear() {
		for (SourceGenerator generator : sourceGenerators.values()) {
			generator.clear();
		}
		taskScheduler.clearTasks();
	}

	public String getGeneratedCode(int language) {
		SourceGenerator generator = sourceGenerators.get(language);
		if (generator != null) {
			return generator.getSource();
		} else {
			return null;
		}
	}

	protected static class Task {

		protected static final Task EARLY_TASK = new Task(Long.MIN_VALUE, null);
		protected final long time;
		protected final Runnable task;

		private Task(long time, Runnable task) {
			this.time = time;
			this.task = task;
		}
	}
}
