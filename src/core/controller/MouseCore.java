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

	protected MouseCore(Robot controller) {
		this.controller = controller;
	}

	/**
	 * Get current position of the mouse
	 * @return Point2D representing the position of the mouse on the screen
	 */
	public Point getPosition() {
		return MouseInfo.getPointerInfo().getLocation();
	}

	/**
	 * Get color of the screen at the position
	 * @param p point p at which the color on the screen will be retrieved
	 * @return Color object: color of the pixel at that point
	 */
	public Color getColor(Point p) {
		return controller.getPixelColor(p.x, p.y);
	}

	/**
	 * Get color of the pixel at point (x,y)
	 * @param x x coordinate on screen
	 * @param y y coordinate on screen
	 * @return Color object: color of the pixel at that opint
	 */
	public Color getColor(int x, int y) {
		return controller.getPixelColor(x, y);
	}

	/**
	 * Get color of the pixel at current mouse position
	 * @return color of the pixel at current mouse position
	 */
	public Color getColor() {
		return getColor(getPosition());
	}

	/**
	 * Click a mouse mask with default hold delay {@value CLICK_DURATION_MS}
	 * @param mask mouse mask. See {@link java.awt.event.InputEvent} class
	 * @throws InterruptedException
	 */
	public void click(int mask) throws InterruptedException {
		hold(mask, CLICK_DURATION_MS);
	}

	/**
	 * Click a mouse mask with delay
	 * @param mask mouse mask. See {@link java.awt.event.InputEvent} class
	 * @param delay delay in milliseconds
	 * @throws InterruptedException
	 */
	public void click(int mask, int delay) throws InterruptedException {
		hold(mask, delay);
	}

	/**
	 * Left click the mouse
	 * @throws InterruptedException
	 */
	public void leftClick() throws InterruptedException {
		click(InputEvent.BUTTON1_MASK);
	}

	/**
	 * Left click the mouse with
	 * @param delay
	 * @throws InterruptedException
	 */
	public void leftClick(int delay) throws InterruptedException {
		click(InputEvent.BUTTON1_MASK, delay);
	}

	/**
	 * Right click mouse with default delay
	 * @throws InterruptedException
	 */
	public void rightClick() throws InterruptedException {
		click(InputEvent.BUTTON3_MASK);
	}

	/**
	 * Right click with certain delay
	 * @param delay delay in milliseconds
	 * @throws InterruptedException
	 */
	public void rightClick(int delay) throws InterruptedException {
		click(InputEvent.BUTTON3_MASK, delay);
	}

	/**
	 * Hold mouse for certain duration
	 * @param mask mouse mask. See {@link java.awt.event.InputEvent} class
	 * @param duration duration in milliseconds
	 * @throws InterruptedException
	 */
	public void hold(int mask, int duration) throws InterruptedException {
		controller.mousePress(mask);

		if (duration >= 0) {
			Thread.sleep(duration);
			controller.mouseRelease(mask);
		}
	}

	/**
	 * Click mouse with default delay at certain position
	 * @param mask mouse mask. See {@link java.awt.event.InputEvent} class
	 * @param x x position
	 * @param y y position
	 * @throws InterruptedException
	 */
	public void click(int mask, int x, int y) throws InterruptedException {
		move(x, y);
		click(mask);
	}

	/**
	 * Click mouse at a point
	 * @param mask mouse mask. See {@link java.awt.event.InputEvent} class
	 * @param p point p to click mouse
	 * @throws InterruptedException
	 */
	public void click(int mask, Point p) throws InterruptedException {
		click(mask, p.x, p.y);
	}

	/**
	 * Left click mouse at a point
	 * @param x x coordinate of the point
	 * @param y y coordinate of the point
	 * @throws InterruptedException
	 */
	public void leftClick(int x, int y) throws InterruptedException {
		click(InputEvent.BUTTON1_MASK, x, y);
	}

	/**
	 * Left click mouse at a point with specified delay
	 * @param x x coordinate of the point
	 * @param y y coordinate of the point
	 * @param delay amount of delay in ms
	 * @throws InterruptedException
	 */
	public void leftClick(int x, int y, int delay) throws InterruptedException {
		move(x, y);
		click(InputEvent.BUTTON1_MASK, delay);
	}

	/**
	 * Right click mouse at a point
	 * @param x x coordinate of the point
	 * @param y y coordinate of the point
	 * @throws InterruptedException
	 */
	public void rightClick(int x, int y) throws InterruptedException {
		click(InputEvent.BUTTON3_MASK, x, y);
	}

	/**
	 * Right click mouse at a point with specified delay
	 * @param x x coordinate of the point
	 * @param y y coordinate of the point
	 * @param delay amount of delay in ms
	 * @throws InterruptedException
	 */
	public void rightClick(int x, int y, int delay) throws InterruptedException {
		move(x, y);
		click(InputEvent.BUTTON3_MASK, delay);
	}

	/**
	 * Press mouse mask
	 * @param mask mouse mask. See {@link java.awt.event.InputEvent} class
	 */
	public void press(int mask) {
		controller.mousePress(mask);
	}

	/**
	 * Release mouse mask
	 * @param mask mouse mask. See {@link java.awt.event.InputEvent} class
	 */
	public void release(int mask) {
		controller.mouseRelease(mask);
	}

	/**
	 * Release 3 primary mouse masks: 1, 2, and 3
	 */
	public void releaseAll() {
		controller.mouseRelease(InputEvent.BUTTON1_MASK);
		controller.mouseRelease(InputEvent.BUTTON2_MASK);
		controller.mouseRelease(InputEvent.BUTTON3_MASK);
	}

	/**
	 * Move mouse to a position on screen
	 * @param newX x position
	 * @param newY y position
	 */
	public void move(int newX, int newY) {
		controller.mouseMove(newX, newY);
	}

	/**
	 * Move mouse to a position on screen
	 * @param p Point p represents position
	 */
	public void move(Point p) {
		move(p.x, p.y);
	}

	/**
	 * Drag a mouse from a point to another point (i.e. left mask down during mouse movement)
	 * @param sourceX x coordinate of the beginning point
	 * @param sourceY y coordinate of the beginning point
	 * @param destX x coordinate of the end point
	 * @param destY y coordinate of the end point
	 */
	public void drag(int sourceX, int sourceY, int destX, int destY) {
		move(sourceX, sourceY);
		press(InputEvent.BUTTON1_MASK);
		move(destX, destY);
		release(InputEvent.BUTTON1_MASK);
	}

	/**
	 * Move mouse by a certain amount
	 * @param amountX x amount to move mouse by
	 * @param amountY y amount to move mouse by
	 */
	public void moveBy(int amountX, int amountY) {
		Point p = getPosition();
		move(p.x + amountX, p.y + amountY);
	}

	/**
	 * Drag a mouse from by a distance (i.e. left mask down during mouse movement)
	 * @param sourceX x coordinate of the beginning point
	 * @param sourceY y coordinate of the beginning point
	 */
	public void dragBy(int amountX, int amountY) {
		press(InputEvent.BUTTON1_MASK);
		moveBy(amountX, amountY);
		release(InputEvent.BUTTON1_MASK);
	}

	/**
	 * Not fully supported. Use at own risk
	 * Move mouse in a grid (defined by topLeft, bottomRight, number of column and number of row)
	 * and perform an action at each point on the grid
	 * @param topLeft topLeft coordinate of grid
	 * @param bottomRight bottomRight coordinate of grid
	 * @param col number of column of grid
	 * @param row number of row of grid
	 * @param action action to perform.
	 */
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
