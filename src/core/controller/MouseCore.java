package core.controller;

import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;

import utilities.Function;

public class MouseCore {

	public static final int CLICK_DURATION_MS = 100;
	private final Robot controller;

	public MouseCore(Robot controller) {
		this.controller = controller;
	}

	public Point getPosition() {
		return MouseInfo.getPointerInfo().getLocation();
	}

	public Color getColor(Point p) {
		return controller.getPixelColor(p.x, p.y);
	}

	public Color getColor(int x, int y) {
		return controller.getPixelColor(x, y);
	}

	public Color getColor() {
		return getColor(getPosition());
	}

	public void click(int mask) throws InterruptedException {
		hold(mask, CLICK_DURATION_MS);
	}

	public void click(int mask, int delay) throws InterruptedException {
		hold(mask, delay);
	}

	public void leftClick() throws InterruptedException {
		click(InputEvent.BUTTON1_MASK);
	}

	public void leftClick(int delay) throws InterruptedException {
		click(InputEvent.BUTTON1_MASK, delay);
	}

	public void rightClick() throws InterruptedException {
		click(InputEvent.BUTTON3_MASK);
	}

	public void rightClick(int delay) throws InterruptedException {
		click(InputEvent.BUTTON3_MASK, delay);
	}

	public void hold(int mask, int duration) throws InterruptedException {
		controller.mousePress(mask);

		if (duration >= 0) {
			Thread.sleep(duration);
			controller.mouseRelease(mask);
		}
	}

	public void press(int mask) {
		controller.mousePress(mask);
	}

	public void release(int mask) {
		controller.mouseRelease(mask);
	}

	public void move(int newX, int newY) {
		controller.mouseMove(newX, newY);
	}

	public void move(Point p) {
		move(p.x, p.y);
	}

	public void moveBy(int amountX, int amountY) {
		Point p = getPosition();
		controller.mouseMove(p.x + amountX, p.y + amountY);
	}

	public void moveArea(Point topLeft, Point bottomRight, int col, int row, Function<Point, Void> action) {
		if (col < 1 || row < 1) {
			return;
		}

		Point current = new Point(topLeft.x, topLeft.y);

		int xIncrement = (bottomRight.x - topLeft.x) / col;
		int yIncrement = (bottomRight.y - topLeft.y) / row;

		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				move(current);
				action.apply(current);

				current.x += xIncrement;

				if (current.x > bottomRight.x) {
					current.x = topLeft.x;
					current.y += yIncrement;
				}
			}
		}
	}
}
