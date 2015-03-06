package globalListener;

import org.jnativehook.GlobalScreen;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

import utilities.Function;

public class GlobalMouseListener implements NativeMouseInputListener, GlobalListener {

	private Function<NativeMouseEvent, Boolean> mousePressed;
	private Function<NativeMouseEvent, Boolean> mouseReleased;
	private Function<NativeMouseEvent, Boolean> mouseMoved;

	public GlobalMouseListener() {
		mousePressed = new Function<NativeMouseEvent, Boolean>() {
			@Override
			public Boolean apply(NativeMouseEvent r) {
				return true;
			}
		};

		mouseReleased = new Function<NativeMouseEvent, Boolean>() {
			@Override
			public Boolean apply(NativeMouseEvent r) {
				return true;
			}
		};

		mouseMoved = new Function<NativeMouseEvent, Boolean>() {
			@Override
			public Boolean apply(NativeMouseEvent r) {
				return true;
			}
		};
	}

	@Override
	public void nativeMouseClicked(NativeMouseEvent arg0) {

	}

	@Override
	public void nativeMousePressed(NativeMouseEvent arg0) {
		if (mousePressed != null) {
			if (!mousePressed.apply(arg0)) {
				System.out.println("Mouse pressed event callback failed");
			}
		}
	}

	@Override
	public void nativeMouseReleased(NativeMouseEvent arg0) {
		if (mouseReleased != null) {
			if (!mouseReleased.apply(arg0)) {
				System.out.println("Mouse release event callback failed");
			}
		}
	}

	@Override
	public void nativeMouseDragged(NativeMouseEvent arg0) {
	}

	@Override
	public void nativeMouseMoved(NativeMouseEvent arg0) {
		if (mouseMoved != null) {
			if (!mouseMoved.apply(arg0)) {
				System.out.println("Mouse move event callback failed");
			}
		}
	}

	public Function<NativeMouseEvent, Boolean> getMousePressed() {
		return mousePressed;
	}

	public void setMousePressed(Function<NativeMouseEvent, Boolean> mousePressed) {
		this.mousePressed = mousePressed;
	}

	public Function<NativeMouseEvent, Boolean> getMouseReleased() {
		return mouseReleased;
	}

	public void setMouseReleased(Function<NativeMouseEvent, Boolean> mouseReleased) {
		this.mouseReleased = mouseReleased;
	}

	public Function<NativeMouseEvent, Boolean> getMouseMoved() {
		return mouseMoved;
	}

	public void setMouseMoved(Function<NativeMouseEvent, Boolean> mouseMoved) {
		this.mouseMoved = mouseMoved;
	}

	@Override
	public boolean startListening() {
		GlobalScreen.addNativeMouseListener(this);
		GlobalScreen.addNativeMouseMotionListener(this);
		return true;
	}

	@Override
	public boolean stopListening() {
		GlobalScreen.removeNativeMouseListener(this);
		GlobalScreen.removeNativeMouseMotionListener(this);
		return true;
	}
}
