package core.controller.internals;

import java.awt.Color;
import java.awt.Point;
import java.util.Arrays;
import java.util.List;

public class AggregateMouseCore extends AbstractMouseCoreImplementation {

	private final List<AbstractMouseCoreImplementation> mice;

	private AggregateMouseCore(List<AbstractMouseCoreImplementation> mice) {
		this.mice = mice;
	}

	public static AggregateMouseCore of(List<AbstractMouseCoreImplementation> mice) {
		if (mice == null || mice.isEmpty()) {
			mice = Arrays.asList(NoopMouseCore.of());
		}

		return new AggregateMouseCore(mice);
	}

	@Override
	public Point getPosition() {
		return mice.iterator().next().getPosition();
	}

	@Override
	public Color getColor(int x, int y) {
		return mice.iterator().next().getColor(x, y);
	}

	@Override
	public Color getColor() {
		return mice.iterator().next().getColor();
	}

	@Override
	public void hold(int mask, int duration) throws InterruptedException {
		for (AbstractMouseCoreImplementation mouse : mice) {
			mouse.hold(mask, duration);
		}
	}

	@Override
	public void hold(int mask, int x, int y, int duration) throws InterruptedException {
		for (AbstractMouseCoreImplementation mouse : mice) {
			mouse.hold(mask, x, y, duration);
		}
	}

	@Override
	public void press(int mask) {
		for (AbstractMouseCoreImplementation mouse : mice) {
			mouse.press(mask);
		}
	}

	@Override
	public void release(int mask) {
		for (AbstractMouseCoreImplementation mouse : mice) {
			mouse.release(mask);
		}
	}

	@Override
	public void move(int newX, int newY) {
		for (AbstractMouseCoreImplementation mouse : mice) {
			mouse.move(newX, newY);
		}
	}

	@Override
	public void drag(int sourceX, int sourceY, int destX, int destY) {
		for (AbstractMouseCoreImplementation mouse : mice) {
			mouse.drag(sourceX, sourceY, destX, destY);
		}
	}

	@Override
	public void moveBy(int amountX, int amountY) {
		for (AbstractMouseCoreImplementation mouse : mice) {
			mouse.moveBy(amountX, amountY);
		}
	}

	@Override
	public void dragBy(int amountX, int amountY) {
		for (AbstractMouseCoreImplementation mouse : mice) {
			mouse.dragBy(amountX, amountY);
		}
	}
}
