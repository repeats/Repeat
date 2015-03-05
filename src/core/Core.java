package core;

import java.awt.AWTException;
import java.awt.Robot;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class Core {

	private ScheduledThreadPoolExecutor executor;
	private Robot controller;

	private MouseCore mouse;
	private KeyboardCore keyboard;

	public Core() {
		try {
			controller = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
			System.exit(1);
		}

		executor = new ScheduledThreadPoolExecutor(10);
		mouse = new MouseCore(controller);
		keyboard = new KeyboardCore(controller);
	}

	public void wait(int duration, Runnable callBack) {
		executor.schedule(callBack, duration, TimeUnit.MILLISECONDS);
	}

	public MouseCore mouse() {
		return mouse;
	}

	public KeyboardCore keyBoard() {
		return keyboard;
	}
}
