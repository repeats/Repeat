package core.webui.server.handlers.renderedobjects;

import core.keyChain.TaskActivation;

public class RenderedGlobalActivation {
	private boolean onKeyPressed;
	private boolean onKeyReleased;

	private RenderedGlobalActivation(boolean onKeyPressed, boolean onKeyReleased) {
		this.onKeyPressed = onKeyPressed;
		this.onKeyReleased = onKeyReleased;
	}

	public static RenderedGlobalActivation fromActivation(TaskActivation activation) {
		return new RenderedGlobalActivation(activation.getGlobalActivation().isOnKeyPressed(), activation.getGlobalActivation().isOnKeyReleased());
	}

	public boolean isOnKeyPressed() {
		return onKeyPressed;
	}

	public void setOnKeyPressed(boolean onKeyPressed) {
		this.onKeyPressed = onKeyPressed;
	}

	public boolean isOnKeyReleased() {
		return onKeyReleased;
	}

	public void setOnKeyReleased(boolean onKeyReleased) {
		this.onKeyReleased = onKeyReleased;
	}
}
