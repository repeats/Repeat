package core.controller;

import java.awt.AWTException;
import java.awt.Robot;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Core {

	private final ScheduledThreadPoolExecutor executor;
	private Robot controller;

	private final MouseCore mouse;
	private final KeyboardCore keyboard;

	public Core() {
		try {
			controller = new Robot();
		} catch (AWTException e) {
			Logger.getLogger(Core.class.getName()).log(Level.SEVERE, "Exception constructing controller", e);
			System.exit(1);
		}

		executor = new ScheduledThreadPoolExecutor(10);
		mouse = new MouseCore(controller);
		keyboard = new KeyboardCore(controller);
	}

	/**
	 * Unsafe method since not interruptible. Use at own risk
	 * @param duration duration for controller to wait
	 * @param callBack action to perform after wait duration
	 */
	protected void wait(int duration, Runnable callBack) {
		executor.schedule(callBack, duration, TimeUnit.MILLISECONDS);
	}

	/**
	 * Blocking wait the current action for an amount of time
	 * @param duration wait duration in milliseconds
	 * @throws InterruptedException
	 */
	public void blockingWait(int duration) throws InterruptedException {
		Thread.sleep(duration);
	}

	/**
	 * Getter for the mouse attribute. See {@link core.controller.MouseCore} class
	 * @return The mouse controller attribute
	 */
	public MouseCore mouse() {
		return mouse;
	}

	/**
	 * Getter for the mouse attribute. See {@link core.controller.KeyboardCore} class
	 * @return The keyboard controller attribute
	 */
	public KeyboardCore keyBoard() {
		return keyboard;
	}
}
