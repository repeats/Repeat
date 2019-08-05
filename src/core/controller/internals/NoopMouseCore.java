package core.controller.internals;

import java.awt.Color;
import java.awt.Point;

public class NoopMouseCore extends AbstractMouseCoreImplementation {

	private static final NoopMouseCore INSTANCE = new NoopMouseCore();

	private NoopMouseCore() {}

	public static NoopMouseCore of() {
		return INSTANCE;
	}

	@Override
	public Point getPosition() {
		return new Point(0, 0);
	}

	@Override
	public Color getColor(int x, int y) {
		return new Color(0, 0, 0);
	}

	@Override
	public Color getColor() {
		return new Color(0, 0, 0);
	}

	@Override
	public void hold(int mask, int duration) throws InterruptedException {}

	@Override
	public void hold(int mask, int x, int y, int duration) throws InterruptedException {}

	@Override
	public void press(int mask) {}

	@Override
	public void release(int mask) {}

	@Override
	public void move(int newX, int newY) {}

	@Override
	public void drag(int sourceX, int sourceY, int destX, int destY) {}

	@Override
	public void moveBy(int amountX, int amountY) {}

	@Override
	public void dragBy(int amountX, int amountY) {}
}
