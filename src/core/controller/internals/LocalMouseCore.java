package core.controller.internals;

import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;

public class LocalMouseCore extends AbstractMouseCoreImplementation {

	private final Robot controller;

	/**
	 * Only use this constructor within the {@link core.controller.Core} class.
	 */
	public LocalMouseCore(Robot controller) {
		this.controller = controller;
	}

	@Override
	public Point getPosition() {
		return MouseInfo.getPointerInfo().getLocation();
	}

	@Override
	public Color getColor(int x, int y) {
		return controller.getPixelColor(x, y);
	}

	@Override
	public Color getColor() {
		return getColor(getPosition());
	}

	@Override
	public void hold(int mask, int duration) throws InterruptedException {
		controller.mousePress(mask);

		if (duration >= 0) {
			Thread.sleep(duration);
			controller.mouseRelease(mask);
		}
	}

	@Override
	public void hold(int mask, int x, int y, int duration) throws InterruptedException {
		move(x, y);
		hold(mask, duration);
	}

	@Override
	public void press(int mask) {
		controller.mousePress(mask);
	}

	@Override
	public void release(int mask) {
		controller.mouseRelease(mask);
	}

	@Override
	public void move(int newX, int newY) {
		controller.mouseMove(newX, newY);
	}

	@Override
	public void drag(int sourceX, int sourceY, int destX, int destY) {
		move(sourceX, sourceY);
		press(InputEvent.BUTTON1_DOWN_MASK);
		move(destX, destY);
		release(InputEvent.BUTTON1_DOWN_MASK);
	}

	@Override
	public void moveBy(int amountX, int amountY) {
		Point p = getPosition();
		move(p.x + amountX, p.y + amountY);
	}

	@Override
	public void dragBy(int amountX, int amountY) {
		press(InputEvent.BUTTON1_DOWN_MASK);
		moveBy(amountX, amountY);
		release(InputEvent.BUTTON1_DOWN_MASK);
	}
}
