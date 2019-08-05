package core.controller.internals;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.InputEvent;

public abstract class AbstractMouseCoreImplementation {

	public static final int CLICK_DURATION_MS = 100;

	/**
	 * Get current position of the mouse
	 * @return Point2D representing the position of the mouse on the screen
	 */
	public abstract Point getPosition();

	/**
	 * Get color of the screen at the position
	 * @param p point p at which the color on the screen will be retrieved
	 * @return Color object: color of the pixel at that point
	 */
	public final Color getColor(Point p) {
		return getColor(p.x, p.y);
	}

	/**
	 * Get color of the pixel at point (x,y)
	 * @param x x coordinate on screen
	 * @param y y coordinate on screen
	 * @return Color object: color of the pixel at that point
	 */
	public abstract Color getColor(int x, int y);

	/**
	 * Get color of the pixel at current mouse position
	 * @return color of the pixel at current mouse position
	 */
	public abstract Color getColor();

	/**
	 * Click a mouse mask with default hold delay {@value CLICK_DURATION_MS}
	 * @param mask mouse mask. See {@link java.awt.event.InputEvent} class
	 * @throws InterruptedException
	 */
	public final void click(int mask) throws InterruptedException {
		hold(mask, CLICK_DURATION_MS);
	}

	/**
	 * Click a mouse mask with delay
	 * @param mask mouse mask. See {@link java.awt.event.InputEvent} class
	 * @param delay delay in milliseconds
	 * @throws InterruptedException
	 */
	public final void click(int mask, int delay) throws InterruptedException {
		hold(mask, delay);
	}

	/**
	 * Left click the mouse
	 * @throws InterruptedException
	 */
	public final void leftClick() throws InterruptedException {
		click(InputEvent.BUTTON1_DOWN_MASK);
	}

	/**
	 * Left click the mouse with
	 * @param delay
	 * @throws InterruptedException
	 */
	public final void leftClick(int delay) throws InterruptedException {
		click(InputEvent.BUTTON1_DOWN_MASK, delay);
	}

	/**
	 * Right click mouse with default delay
	 * @throws InterruptedException
	 */
	public final void rightClick() throws InterruptedException {
		click(InputEvent.BUTTON3_DOWN_MASK);
	}

	/**
	 * Right click with certain delay
	 * @param delay delay in milliseconds
	 * @throws InterruptedException
	 */
	public final void rightClick(int delay) throws InterruptedException {
		click(InputEvent.BUTTON3_DOWN_MASK, delay);
	}

	/**
	 * Hold mouse for certain duration
	 * @param mask mouse mask. See {@link java.awt.event.InputEvent} class
	 * @param duration duration in milliseconds
	 * @throws InterruptedException
	 */
	public abstract void hold(int mask, int duration) throws InterruptedException;

	/**
	 * Move mouse to a position and hold down for a certain duration
	 *
	 * @param mask mask to hold down
	 * @param x x-coordinate to hold down at
	 * @param y y-coordinate to hold down at
	 * @param duration hold down duration in milliseconds
	 * @throws InterruptedException
	 */
	public abstract void hold(int mask, int x, int y, int duration) throws InterruptedException;

	/**
	 * Click mouse with default delay at certain position
	 * @param mask mouse mask. See {@link java.awt.event.InputEvent} class
	 * @param x x position
	 * @param y y position
	 * @throws InterruptedException
	 */
	public final void click(int mask, int x, int y) throws InterruptedException {
		hold(mask, x, y, CLICK_DURATION_MS);
	}

	/**
	 * Click mouse at a point
	 * @param mask mouse mask. See {@link java.awt.event.InputEvent} class
	 * @param p point p to click mouse
	 * @throws InterruptedException
	 */
	public final void click(int mask, Point p) throws InterruptedException {
		click(mask, p.x, p.y);
	}

	/**
	 * Left click mouse at a point
	 * @param x x coordinate of the point
	 * @param y y coordinate of the point
	 * @throws InterruptedException
	 */
	public final void leftClick(int x, int y) throws InterruptedException {
		click(InputEvent.BUTTON1_DOWN_MASK, x, y);
	}

	/**
	 * Left click mouse at a point with specified delay
	 * @param x x coordinate of the point
	 * @param y y coordinate of the point
	 * @param delay amount of delay in ms
	 * @throws InterruptedException
	 */
	public final void leftClick(int x, int y, int delay) throws InterruptedException {
		hold(InputEvent.BUTTON1_DOWN_MASK, x, y, delay);
	}

	/**
	 * Right click mouse at a point
	 * @param x x coordinate of the point
	 * @param y y coordinate of the point
	 * @throws InterruptedException
	 */
	public final void rightClick(int x, int y) throws InterruptedException {
		hold(InputEvent.BUTTON3_DOWN_MASK, x, y, CLICK_DURATION_MS);
	}

	/**
	 * Right click mouse at a point with specified delay
	 * @param x x coordinate of the point
	 * @param y y coordinate of the point
	 * @param delay amount of delay in ms
	 * @throws InterruptedException
	 */
	public final void rightClick(int x, int y, int delay) throws InterruptedException {
		hold(InputEvent.BUTTON3_DOWN_MASK, x, y, delay);
	}

	/**
	 * Press mouse mask
	 * @param mask mouse mask. See {@link java.awt.event.InputEvent} class
	 */
	public abstract void press(int mask);

	/**
	 * Release mouse mask
	 * @param mask mouse mask. See {@link java.awt.event.InputEvent} class
	 */
	public abstract void release(int mask);

	/**
	 * Release 3 primary mouse masks: 1, 2, and 3
	 */
	public final void releaseAll() {
		release(InputEvent.BUTTON1_DOWN_MASK);
		release(InputEvent.BUTTON2_DOWN_MASK);
		release(InputEvent.BUTTON3_DOWN_MASK);
	}

	/**
	 * Move mouse to a position on screen
	 * @param newX x position
	 * @param newY y position
	 */
	public abstract void move(int newX, int newY);

	/**
	 * Move mouse to a position on screen
	 * @param p Point p represents position
	 */
	public final void move(Point p) {
		move(p.x, p.y);
	}

	/**
	 * Drag a mouse from a point to another point (i.e. left mask down during mouse movement)
	 * @param sourceX x coordinate of the beginning point
	 * @param sourceY y coordinate of the beginning point
	 * @param destX x coordinate of the end point
	 * @param destY y coordinate of the end point
	 */
	public abstract void drag(int sourceX, int sourceY, int destX, int destY);

	/**
	 * Move mouse by a certain amount
	 * @param amountX x amount to move mouse by
	 * @param amountY y amount to move mouse by
	 */
	public abstract void moveBy(int amountX, int amountY);

	/**
	 * Drag a mouse from by a distance (i.e. left mask down during mouse movement)
	 * @param sourceX x coordinate of the beginning point
	 * @param sourceY y coordinate of the beginning point
	 */
	public abstract void dragBy(int amountX, int amountY);
}
