package org.simplenativehooks.events;

public class NativeMouseEvent {
	public enum State {
		UNKNOWN,
		MOVED,
		PRESSED,
		RELEASED,
		SCROLLED;
	}

	// Provided x and y correspond to the values returned by
	// java.awt.PointerInfo.getPointerInfo().getLocation().
	private int x;
	private int y;

	private State state;

	// Button is one of the following.
	// java.awt.event.InputEvent.BUTTON1_DOWN_MASK
	// java.awt.event.InputEvent.BUTTON2_DOWN_MASK
	// java.awt.event.InputEvent.BUTTON3_DOWN_MASK
	private int button;

	private NativeMouseEvent(int x, int y, State state, int button) {
		this.x = x;
		this.y = y;
		this.state = state;
		this.button = button;
	}

	public static NativeMouseEvent of(int x, int y, State state, int button) {
		return new NativeMouseEvent(x, y, state, button);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public State getState() {
		return state;
	}

	public int getButton() {
		return button;
	}
}
