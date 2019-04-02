package globalListener;

import org.simplenativehooks.NativeMouseEvent;

import utilities.Function;

public abstract class AbstractGlobalMouseListener implements GlobalListener {

	protected Function<NativeMouseEvent, Boolean> mousePressed;
	protected Function<NativeMouseEvent, Boolean> mouseReleased;
	protected Function<NativeMouseEvent, Boolean> mouseMoved;

	protected AbstractGlobalMouseListener() {
		mousePressed = Function.<NativeMouseEvent>trueFunction();
		mouseReleased = Function.<NativeMouseEvent>trueFunction();
		mouseMoved = Function.<NativeMouseEvent>trueFunction();
	}

	public final void setMousePressed(Function<NativeMouseEvent, Boolean> mousePressed) {
		this.mousePressed = mousePressed;
	}
	public final void setMouseReleased(Function<NativeMouseEvent, Boolean> mouseReleased) {
		this.mouseReleased = mouseReleased;
	}
	public final void setMouseMoved(Function<NativeMouseEvent, Boolean> mouseMoved) {
		this.mouseMoved = mouseMoved;
	}
}
