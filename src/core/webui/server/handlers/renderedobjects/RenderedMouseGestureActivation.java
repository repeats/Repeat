package core.webui.server.handlers.renderedobjects;

import java.util.Set;

import core.keyChain.MouseGesture;
import core.keyChain.TaskActivation;

public class RenderedMouseGestureActivation {
	private boolean alpha;
	private boolean circleLeft;
	private boolean gamma;
	private boolean greaterThan;
	private boolean hat;
	private boolean lessThan;
	private boolean n;
	private boolean square;
	private boolean squareRoot;
	private boolean tilda;
	private boolean triangle;
	private boolean z;

	public static RenderedMouseGestureActivation fromActivation(TaskActivation activation) {
		Set<MouseGesture> gestures = activation.getMouseGestures();
		RenderedMouseGestureActivation output = new RenderedMouseGestureActivation();
		output.alpha = gestures.contains(MouseGesture.ALPHA);
		output.circleLeft = gestures.contains(MouseGesture.CIRCLE_LEFT);
		output.gamma = gestures.contains(MouseGesture.GAMMA);
		output.greaterThan = gestures.contains(MouseGesture.GREATER_THAN);
		output.hat = gestures.contains(MouseGesture.HAT);
		output.lessThan = gestures.contains(MouseGesture.LESS_THAN);
		output.n = gestures.contains(MouseGesture.N);
		output.square = gestures.contains(MouseGesture.SQUARE);
		output.squareRoot = gestures.contains(MouseGesture.SQUARE_ROOT);
		output.tilda = gestures.contains(MouseGesture.TILDA);
		output.triangle = gestures.contains(MouseGesture.TRIANGLE);
		output.z = gestures.contains(MouseGesture.Z);
		return output;
	}

	public boolean isAlpha() {
		return alpha;
	}

	public void setAlpha(boolean alpha) {
		this.alpha = alpha;
	}

	public boolean isCircleLeft() {
		return circleLeft;
	}

	public void setCircleLeft(boolean circleLeft) {
		this.circleLeft = circleLeft;
	}

	public boolean isGamma() {
		return gamma;
	}

	public void setGamma(boolean gamma) {
		this.gamma = gamma;
	}

	public boolean isGreaterThan() {
		return greaterThan;
	}

	public void setGreaterThan(boolean greaterThan) {
		this.greaterThan = greaterThan;
	}

	public boolean isHat() {
		return hat;
	}

	public void setHat(boolean hat) {
		this.hat = hat;
	}

	public boolean isLessThan() {
		return lessThan;
	}

	public void setLessThan(boolean lessThan) {
		this.lessThan = lessThan;
	}

	public boolean isN() {
		return n;
	}

	public void setN(boolean n) {
		this.n = n;
	}

	public boolean isSquare() {
		return square;
	}

	public void setSquare(boolean square) {
		this.square = square;
	}

	public boolean isSquareRoot() {
		return squareRoot;
	}

	public void setSquareRoot(boolean squareRoot) {
		this.squareRoot = squareRoot;
	}

	public boolean isTilda() {
		return tilda;
	}

	public void setTilda(boolean tilda) {
		this.tilda = tilda;
	}

	public boolean isTriangle() {
		return triangle;
	}

	public void setTriangle(boolean triangle) {
		this.triangle = triangle;
	}

	public boolean isZ() {
		return z;
	}

	public void setZ(boolean z) {
		this.z = z;
	}
}
