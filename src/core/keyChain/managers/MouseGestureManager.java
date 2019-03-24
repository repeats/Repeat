package core.keyChain.managers;

import java.awt.Point;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import core.config.Config;
import core.keyChain.KeyStroke;
import core.keyChain.MouseGesture;
import core.keyChain.TaskActivation;
import core.keyChain.mouseGestureRecognition.MouseGestureClassifier;
import core.userDefinedTask.UserDefinedAction;
import globalListener.AbstractGlobalMouseListener;
import globalListener.GlobalListenerFactory;
import globalListener.NativeMouseEvent;
import utilities.Function;

/**
 * Class to manage mouse gesture recognition and action
 */
public class MouseGestureManager extends KeyStrokeManager {

	private static final Logger LOGGER = Logger.getLogger(MouseGestureManager.class.getName());

	private static final int MAX_COORDINATES_COUNT = 1000;

	private final MouseGestureClassifier mouseGestureRecognizer;
	private final Map<MouseGesture, UserDefinedAction> actionMap;
	private final AbstractGlobalMouseListener mouseListener;
	private final Queue<Point> coordinates;
	private boolean enabled;

	public MouseGestureManager(Config config) {
		super(config);
		mouseGestureRecognizer = new MouseGestureClassifier();
		actionMap = new HashMap<>();
		coordinates = new ConcurrentLinkedQueue<Point>();
		mouseListener = GlobalListenerFactory.of().createGlobalMouseListener();
	}

	/**
	 * Start listening to the mouse for movement
	 */
	@Override
	public void startListening() {
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

	@Override
	public Set<UserDefinedAction> onKeyStrokePressed(KeyStroke stroke) {
		if (stroke.getKey() == getConfig().getMouseGestureActivationKey()) {
			startRecording();
		}
		return Collections.<UserDefinedAction>emptySet();
	}

	@Override
	public Set<UserDefinedAction> onKeyStrokeReleased(KeyStroke stroke) {
		if (stroke.getKey() == getConfig().getMouseGestureActivationKey()) {
			UserDefinedAction action = finishRecording();
			return new HashSet<>(Arrays.asList(action));
		}
		return Collections.<UserDefinedAction>emptySet();
	}

	@Override
	public void clear() {
		enabled = false;
		coordinates.clear();
	}

	/**
	 * Check if any collision between the gestures set and the set of currently registered gestures
	 *
	 * @param gesture gesture set to check
	 * @return set of any collision occurs
	 */
	@Override
	public Set<UserDefinedAction> collision(Collection<TaskActivation> activations) {
		Set<MouseGesture> gestures = activations.stream().map(a -> a.getMouseGestures()).flatMap(Set::stream).collect(Collectors.toSet());

		Set<MouseGesture> collisions = new HashSet<>(actionMap.keySet());
		collisions.retainAll(gestures);

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
	@Override
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
	@Override
	public Set<UserDefinedAction> unRegisterAction(UserDefinedAction action) {
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
	 * Start recording the gesture
	 */
	protected synchronized void startRecording() {
		if (enabled) {
			return;
		}
		coordinates.clear();
		enabled = true;
	}

	/**
	 * Finish recording the gesture. Now decode it.
	 */
	protected synchronized UserDefinedAction finishRecording() {
		enabled = false;
		try {
			MouseGesture gesture = processCurrentData();
			if (MouseGesture.IGNORED_CLASSIFICATIONS.contains(gesture)) {
				return null;
			}

			UserDefinedAction task = actionMap.get(gesture);
			if (task != null) {
				task.setInvoker(TaskActivation.newBuilder().withMouseGesture(gesture).build());
			}
			return task;
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
	 * Stop listening to the mouse for movement
	 */
	protected void stopListening() {
		mouseListener.stopListening();
	}
}
