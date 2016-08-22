package core.keyChain;

import globalListener.GlobalMouseListener;

import java.awt.Point;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.mouse.NativeMouseEvent;

import utilities.Function;
import core.keyChain.mouseGestureRecognition.MouseGestureClassifier;
import core.userDefinedTask.UserDefinedAction;

/**
 * Class to manage mouse gesture recognition and action
 */
public class MouseGestureManager {

	private static final Logger LOGGER = Logger.getLogger(MouseGestureManager.class.getName());

	private static final int MAX_COORDINATES_COUNT = 1000;

	private final MouseGestureClassifier mouseGestureRecognizer;
	private final Map<MouseGesture, UserDefinedAction> actionMap;
	private final GlobalMouseListener mouseListener;
	private final Queue<Point> coordinates;
	private boolean enabled;

	public MouseGestureManager() {
		mouseGestureRecognizer = new MouseGestureClassifier();
		actionMap = new HashMap<>();
		coordinates = new ConcurrentLinkedQueue<Point>();
		mouseListener = new GlobalMouseListener();
	}

	/**
	 * Check if any collision between the gestures set and the set of currently registered gestures
	 *
	 * @param gesture gesture set to check
	 * @return set of any collision occurs
	 */
	public Set<UserDefinedAction> areGesturesRegistered(Collection<MouseGesture> gesture) {
		Set<MouseGesture> collisions = new HashSet<>(actionMap.keySet());
		collisions.retainAll(gesture);

		Set<UserDefinedAction> output = new HashSet<>();
		for (MouseGesture collision : collisions) {
			output.add(actionMap.get(collision));
		}
		return output;
	}

	/**
	 * Register an action associated with a {@link MouseGesture}.
	 *
	 * @param action the action to execute
	 * @return the gestures that are collided
	 */
	public Set<UserDefinedAction> registerAction(UserDefinedAction action) {
		Set<UserDefinedAction> collisions = new HashSet<>();
		for (MouseGesture gesture : action.getActivation().getMouseGestures()) {
			UserDefinedAction collided = actionMap.get(gesture);
			if (collided != null) {
				collisions.add(collided);
			}

			actionMap.put(gesture, action);
		}

		return collisions;
	}

	/**
	 * Unregister the action associated with a {@link MouseGesture}
	 *
	 * @param action action to unregister
	 * @return action (if exist) associated with this gesture
	 */
	protected Set<UserDefinedAction> unRegisterAction(UserDefinedAction action) {
		Set<UserDefinedAction> output = new HashSet<>();
		for (MouseGesture gesture : action.getActivation().getMouseGestures()) {
			UserDefinedAction removed = actionMap.remove(gesture);
			if (removed != null) {
				output.add(removed);
			}
		}

		return output;
	}

	/**
	 * Unregister an action from a {@link MouseGesture} and register it to
	 * another one. This kicks out all other actions associated with this
	 * gesture.
	 *
	 * @param action action to re-register
	 * @param gesture new gestures to register the action with
	 *
	 * @return any action associated with the new gesture previously
	 */
	protected Set<UserDefinedAction> reRegisterAction(UserDefinedAction action, Collection<MouseGesture> gestures) {
		unRegisterAction(action);
		action.getActivation().getMouseGestures().clear();
		action.getActivation().getMouseGestures().addAll(gestures);
		return registerAction(action);
	}

	/**
	 * Start recording the gesture
	 */
	protected void startRecoarding() {
		coordinates.clear();
		enabled = true;
	}

	/**
	 * Finish recording the gesture. Now decode it
	 */
	protected UserDefinedAction finishRecording() {
		enabled = false;
		try {
			MouseGesture gesture = processCurrentData();
			if (MouseGesture.IGNORED_CLASSIFICATIONS.contains(gesture)) {
				return null;
			}
			return actionMap.get(gesture);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to classify recorded data", e);
		}
		return null;
	}

	/**
	 * Process currently stored points and detect any gesture
	 *
	 * @return the detected {@link MouseGesture}
	 * @throws IOException
	 */
	private MouseGesture processCurrentData() throws IOException {
		int size = coordinates.size();
		return mouseGestureRecognizer.classifyGesture(coordinates, size);
	}

	/**
	 * Start listening to the mouse for movement
	 */
	protected void startListening() {
		mouseListener.setMouseMoved(new Function<NativeMouseEvent, Boolean> () {
			@Override
			public Boolean apply(NativeMouseEvent d) {
				if (enabled && coordinates.size() < MAX_COORDINATES_COUNT) {
					coordinates.add(new Point(d.getX(), d.getY()));
				}
				return true;
			}});
		mouseListener.startListening();
	}

	/**
	 * Stop listening to the mouse for movement
	 */
	protected void stopListening() {
		mouseListener.stopListening();
	}
}
