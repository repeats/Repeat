package core;

import globalListener.GlobalKeyListener;
import globalListener.GlobalMouseListener;

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

	private LinkedList<Task> tasks;
	private LinkedList<ScheduledFuture<?>> scheduled;
	private ScheduledThreadPoolExecutor executor;
	private long startTime;

	private GlobalKeyListener keyListener;
	private GlobalMouseListener mouseListener;

	static {
		// Get the logger for "org.jnativehook" and set the level to off.
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);

		// Change the level for all handlers attached to the default logger.
		Handler[] handlers = Logger.getLogger("").getHandlers();
		for (int i = 0; i < handlers.length; i++) {
			handlers[i].setLevel(Level.OFF);
		}
	}

	public Recorder(final Core controller) {
		this.executor = new ScheduledThreadPoolExecutor(100);
		this.tasks = new LinkedList<>();
		this.scheduled = new LinkedList<>();

		/*************************************************************************************************/
		keyListener = new GlobalKeyListener();
		keyListener.setKeyPressed(new Function<NativeKeyEvent, Boolean>() {
			@Override
			public Boolean apply(final NativeKeyEvent r) {
				tasks.add(new Task(System.currentTimeMillis() - startTime, new Runnable() {
					@Override
					public void run() {
						int code = CodeConverter.getKeyEventCode(r.getKeyCode());
						controller.keyBoard().press(code);
					}
				}));
				return true;
			}
		});

		keyListener.setKeyReleased(new Function<NativeKeyEvent, Boolean>() {
			@Override
			public Boolean apply(final NativeKeyEvent r) {
				tasks.add(new Task(System.currentTimeMillis() - startTime, new Runnable() {
					@Override
					public void run() {
						int code = CodeConverter.getKeyEventCode(r.getKeyCode());
						controller.keyBoard().release(code);
					}
				}));
				return true;
			}
		});

		/*************************************************************************************************/
		mouseListener = new GlobalMouseListener();
		mouseListener.setMouseReleased(new Function<NativeMouseEvent, Boolean>() {
			@Override
			public Boolean apply(final NativeMouseEvent r) {
				tasks.add(new Task(System.currentTimeMillis() - startTime, new Runnable() {
					@Override
					public void run() {
						int code = CodeConverter.getMouseButtonCode(r.getButton());
						controller.mouse().release(code);
					}
				}));
				return true;
			}
		});

		mouseListener.setMousePressed(new Function<NativeMouseEvent, Boolean>() {
			@Override
			public Boolean apply(final NativeMouseEvent r) {
				tasks.add(new Task(System.currentTimeMillis() - startTime, new Runnable() {
					@Override
					public void run() {
						int code = CodeConverter.getMouseButtonCode(r.getButton());
						controller.mouse().press(code);
					}
				}));
				return true;
			}
		});

		mouseListener.setMouseMoved(new Function<NativeMouseEvent, Boolean>() {
			@Override
			public Boolean apply(final NativeMouseEvent r) {
				tasks.add(new Task(System.currentTimeMillis() - startTime, new Runnable() {
					@Override
					public void run() {
						controller.mouse().move(r.getX(), r.getY());
					}
				}));
				return true;
			}
		});
	}

	public static void main(String[] args) throws NativeHookException {
		Core c = new Core();
		Recorder r = new Recorder(c);

		try {
			Thread.sleep(1000);
			System.out.println("Started");
			r.record();
			Thread.sleep(4000);
			System.out.println("Stopped");
			r.stopRecord();
			Thread.sleep(4000);
			r.replay();

			Thread.sleep(3000);
			System.out.println(r.executor.getActiveCount());
			r.executor.shutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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

		if (GlobalScreen.isNativeHookRegistered()) {
			GlobalScreen.unregisterNativeHook();
		}
	}

	public void replay() {
		for (Task t : tasks) {
			ScheduledFuture<?> future = executor.schedule(t.task, t.time, TimeUnit.MILLISECONDS);
			scheduled.add(future);
		}
	}

	public void stopReplay() {
		for (ScheduledFuture<?> f : scheduled) {
			f.cancel(false);
		}
		scheduled.clear();
	}

	public void clear() {
		tasks.clear();
		scheduled.clear();
	}

	private static class Task {
		private long time;
		private Runnable task;

		private Task(long time, Runnable task) {
			this.time = time;
			this.task = task;
		}
	}
}
