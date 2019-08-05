package core.controller;

import java.awt.AWTException;
import java.awt.Robot;
import java.util.logging.Level;
import java.util.logging.Logger;

import core.config.Config;
import core.controller.internals.LocalKeyboardCore;
import core.controller.internals.LocalMouseCore;

public final class Core {

	private static final Logger LOGGER = Logger.getLogger(Core.class.getName());

	private final MouseCore mouse;
	private final KeyboardCore keyboard;

	private Core(MouseCore mouse, KeyboardCore keyboard) {
		this.mouse = mouse;
		this.keyboard = keyboard;
	}

	protected static Core local(Config config) {
		Robot controller = null;
		try {
			controller = new Robot();
		} catch (AWTException e) {
			LOGGER.log(Level.SEVERE, "Exception constructing controller", e);
			System.exit(1);
		}

		return new Core(new MouseCore(new LocalMouseCore(controller)), new KeyboardCore(new LocalKeyboardCore(config, controller)));
	}

	public static Core getInstance(MouseCore mouse, KeyboardCore keyboard) {
		return new Core(mouse, keyboard);
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
	 * A short alias for blockingWait
	 * @param duration wait duration in milliseconds
	 * @throws InterruptedException
	 */
	public void delay(int duration) throws InterruptedException {
		blockingWait(duration);
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
