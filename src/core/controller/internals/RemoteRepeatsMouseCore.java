package core.controller.internals;

import java.awt.Color;
import java.awt.Point;

import core.ipc.repeatClient.repeatPeerClient.api.RepeatsClientApi;

public class RemoteRepeatsMouseCore extends AbstractMouseCoreImplementation {

	private RepeatsClientApi api;

	public RemoteRepeatsMouseCore(RepeatsClientApi api) {
		this.api = api;
	}

	@Override
	public Point getPosition() {
		return api.mouse().getPosition();
	}

	@Override
	public Color getColor(int x, int y) {
		return api.mouse().getColor(x, y);
	}

	@Override
	public Color getColor() {
		return api.mouse().getColor();
	}

	@Override
	public void hold(int mask, int duration) throws InterruptedException {
		api.mouse().hold(mask, duration);
	}

	@Override
	public void hold(int mask, int x, int y, int duration) throws InterruptedException {
		api.mouse().hold(mask, x, y, duration);
	}

	@Override
	public void press(int mask) {
		api.mouse().press(mask);
	}

	@Override
	public void release(int mask) {
		api.mouse().release(mask);
	}

	@Override
	public void move(int newX, int newY) {
		api.mouse().move(newX, newY);
	}

	@Override
	public void drag(int sourceX, int sourceY, int destX, int destY) {
		api.mouse().drag(sourceX, sourceY, destX, destY);
	}

	@Override
	public void moveBy(int amountX, int amountY) {
		api.mouse().moveBy(amountX, amountY);
	}

	@Override
	public void dragBy(int amountX, int amountY) {
		api.mouse().dragBy(amountX, amountY);
	}

}
