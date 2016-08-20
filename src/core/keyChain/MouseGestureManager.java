package core.keyChain;

import globalListener.GlobalMouseListener;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jnativehook.mouse.NativeMouseEvent;

import utilities.Function;
import utilities.JSONUtility;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import argo.jdom.JsonStringNode;
import core.userDefinedTask.UserDefinedAction;

/**
 * Class to manage mouse gesture recognition and action
 */
public class MouseGestureManager {

	private static final Logger LOGGER = Logger.getLogger(MouseGestureManager.class.getName());
	private static final int MAX_COORDINATES_COUNT = 1000;
	private static final Set<MouseGesture> IGNORED_CLASSIFICATIONS = new HashSet<>(
			Arrays.asList(MouseGesture.HORIZONTAL,
						  MouseGesture.VERTICAL,
						  MouseGesture.RANDOM));
	private static final String GESTURE_RECOGNITION_SERVER = "http://localhost:8000";

	private final Map<MouseGesture, UserDefinedAction> actionMap;
	private final GlobalMouseListener mouseListener;
	private final Queue<Point> coordinates;
	private boolean enabled;

	public MouseGestureManager() {
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
	public Set<MouseGesture> areGesturesRegistered(Collection<MouseGesture> gesture) {
		Set<MouseGesture> collision = new HashSet<>(actionMap.keySet());
		collision.retainAll(gesture);
		return collision;
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
	 * Show a short notice that collision occurred
	 *
	 * @param parent parent frame to show the notice in (null if there is none)
	 * @param gestures collision gestures
	 */
	public static void showCollisionWarning(JFrame parent, Set<MouseGesture> gestures) {
		JOptionPane.showMessageDialog(parent,
				"Newly registered gestures "
				+ "will collide with previously registered gesture \"" + gestures
				+ "\"\nYou cannot assign this key chain unless you remove the conflicting key chain...",
				"Key chain collision!", JOptionPane.WARNING_MESSAGE);
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
	protected UserDefinedAction finishRecoarding() {
		enabled = false;
		try {
			MouseGesture gesture = processCurrentData();
			return actionMap.get(gesture);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to finish recorded data", e);
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
		List<JsonNode> points = new ArrayList<>(coordinates.size());

		final int size = coordinates.size();
		LOGGER.info("Classifying with size = " + size);

		for (int i = 0 ;i < size; i++) {
			Point p = coordinates.poll();
			JsonNode comer = pointToNode(p);
			points.add(comer);
		}

		return MouseGesture.RANDOM;
//		return post(JsonNodeFactories.object(
//				JsonNodeFactories.field(
//					"data", JsonNodeFactories.array(points))));
	}

	/**
	 * Post the JSON content to {@link #GESTURE_RECOGNITION_SERVER} and process the result
	 * @param content json content to be posted
	 * @return the classification received from the server
	 * @throws IOException
	 */
	private MouseGesture post(JsonNode content) throws IOException {
		byte[] out = JSONUtility.jsonToString(content.getRootNode()).getBytes(Charset.forName("UTF-8"));
		int length = out.length;

		// Construct request
		URL url = new URL(GESTURE_RECOGNITION_SERVER);
		URLConnection con = url.openConnection();
		HttpURLConnection http = (HttpURLConnection)url.openConnection();
		http.setRequestMethod("POST");
		http.setDoOutput(true);
		http.setFixedLengthStreamingMode(length);
		http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

		// Send request
		http.connect();
		try (OutputStream os = http.getOutputStream()) {
		    os.write(out);
		    os.flush();
		}

		// Retrieve response
		int responseCode = http.getResponseCode();
//		System.out.println("\nSending 'POST' request to URL : " + url);
//		System.out.println("Response Code : " + responseCode);
		if (responseCode != 200) {
			throw new RuntimeException("Did not receive 200 from server! Instead status code " + responseCode);
		}

		String responseString = null;
		try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
			String inputLine;
			StringBuilder response = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}

			responseString = response.toString();
		}

		return processPostResponse(responseString);
	}

	/**
	 * Process post response from the server to get the identified classifcation
	 * @param response the response as string from server
	 * @return the identified classification
	 */
	private MouseGesture processPostResponse(String response) {
		JsonRootNode parsed = JSONUtility.jsonFromString(response);
		JsonNode toAnalyze = parsed.getNode("result");
		Map<JsonStringNode, JsonNode> fields = toAnalyze.getFields();
		List<ClassificationEntry> converted = new ArrayList<>(5);
		int totalCount = 0;

		// Parse for meaningful values
		for (Entry<JsonStringNode, JsonNode> entry : fields.entrySet()) {
			String name = entry.getKey().getStringValue();
			MouseGesture named = MouseGesture.find(name);
			if (named == null) {
				throw new IllegalArgumentException("Unable to identify class named " + name);
			}

			int number = Integer.parseInt(entry.getValue().getNumberValue());

			if (IGNORED_CLASSIFICATIONS.contains(named)) {
				continue;
			}

			totalCount += number;
			converted.add(new ClassificationEntry(name, number));
		}

		if (converted.size() == 0) { // Nothing to do
			return MouseGesture.RANDOM;
		} else if (converted.size() == 1) {
			System.out.println(response);
			if (converted.get(0).count > 4) {
				System.out.println("======> " + converted.get(0).name);
				return converted.get(0).name;

			} else {
				return MouseGesture.RANDOM;
			}
		} else {
			// Sort by decreasing count
			Collections.sort(converted, new Comparator<ClassificationEntry>() {
				@Override
				public int compare(ClassificationEntry o1, ClassificationEntry o2) {
					return o2.count - o1.count;
				}
			});

			int max = converted.get(0).count;
			int nextMax = converted.get(1).count;

			System.out.println(response);
			if (((double) max / totalCount > 0.5) && (max - nextMax > 10)) {
				System.out.println("======> " + converted.get(0).name);
				JOptionPane.showMessageDialog(null, "Detected symbol " + converted.get(0).name);
				return converted.get(0).name;
			} else {
				return MouseGesture.RANDOM;
			}
		}
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

	/**
	 * Convert a point to json node to send to the server
	 * @param p point to convert to json node
	 * @return a json node to send to the server representing the input point
	 */
	private static JsonNode pointToNode(Point p) {
		return JsonNodeFactories.array(
				JsonNodeFactories.number(p.x),
				JsonNodeFactories.number(p.y));
	}

	/**
	 * Class representing classification entry from the server
	 */
	private static class ClassificationEntry {
		private final MouseGesture name;
		private final int count;

		private ClassificationEntry(String name, int count) {
			this.name = MouseGesture.find(name);
			if (this.name == null) {
				throw new IllegalArgumentException("Unable to identify classification " + name);
			}

			this.count = count;
		}
	}
}
