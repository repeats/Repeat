package core.ipc.repeatServer.processors;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import core.controller.Core;
import core.ipc.repeatServer.MainMessageSender;
import core.userDefinedTask.Tools;
import utilities.IterableUtility;
import utilities.swing.SwingUtil;

/**
 * This class represents the message processor for action request received from client.
 *
 * The central processor is responsible for extraction of message id and type
 * A received message from the lower layer (central processor) will have the following JSON contents:
 * {
 *		"device": a string from the set {"mouse", "keyboard", "tool"},
 *		"action" : a string specifying action,
 *		"parameters" : a list of parameters for this action
 * }
 *
 *************************************************************************
 * The following actions are supported for mouse:
 * 1a) press(mask): press the mouse using the current mask
 * 1b) release(mask): release the mouse using the current mask
 *
 * 2a) left_click(): left click at the current mouse position
 * 2b) left_click(int): left click with delay in ms
 * 2c) left_click(int, int): left click at a position
 *
 * 3a) right_click(): right click at the current mouse position
 * 3b) right_click(int): right click with delay in ms
 * 3c) right_click(int, int): right click at a position
 *
 * 4) move(int, int): move mouse to a certain position
 * 5) move_by(int, int): move mouse by a certain distance (in pixel)
 *
 * 6) drag(int, int): drag mouse from a point to another point (i.e. leftClick at the current position, then move mouse to end point, then release mouse)
 * 7) drag(int, int, int, int): drag mouse from a point to another point (i.e. leftClick at the starting point, then move mouse to end point, then release mouse)
 * 8) drag_by(int, int): drag mouse by a certain distance
 *
 * 9) get_position(): get position of the mouse
 * 10) get_color(): get the color (RGB) of the current pixel at which the mouse is pointing
 * 11) get_color(int, int): get the color (RGB) of the pixel at the location
 *
 *************************************************************************
 * The following actions are supported for keyboard:
 * 1) press(key_value) : press a key on the keyboard. The int value is the same as defined in java.awt.KeyEvent class
 * 2) release(key_value) : release a key on the keyboard.
 * 2) type(key_values...) : type a series of keys sequentially.
 * 3) type_string(strings...) : type a series of strings sequentially.
 * 4) combination(key_values...) : perform a key combination
 *
 *************************************************************************
 * The following actions are supported for common tool:
 * 1) getClipboard() : get current clipboard text content
 * 2) setClipboard(value) : set current clipboard text content
 * 3) execute(command) : execute a command in a subprocess
 * 4) execute(command, cwd) : execute a command in a subprocess, in a given directory
 * 5) get_selection(title, selected, choices): show a selection panel for user to select choices from
 *
 * Once the action has been performed successfully, a reply message will be sent using the same id received.
 * The received message has the following JSON format in content:
 * {
 * 		"status" : operation status of the action (either SUCCESS or FAILURE),
 * 		"message" : information/debug message if applicable
 * }
 *
 * @author HP Truong
 *
 */
class ControllerRequestProcessor extends AbstractMessageProcessor {

	private static final String DEVICE_MOUSE = "mouse";
	private static final String DEVICE_KEYBOARD = "keyboard";
	private static final String DEVICE_TOOL = "tool";

	private final Core core;

	protected ControllerRequestProcessor(MainMessageSender messageSender, Core core) {
		super(messageSender);
		this.core = core;
	}

	@Override
	public boolean process(String type, long id, JsonNode content) throws InterruptedException {
		final String device = content.getStringValue("device");
		String action = content.getStringValue("action");

		List<Object> parsedParams = parseParams(content.getArrayNode("parameters"));

		if (device.equals(DEVICE_MOUSE)) {
			return mouseAction(type, id, action, parsedParams);
		} else if (device.equals(DEVICE_KEYBOARD)) {
			return keyboardAction(type, id, action, parsedParams);
		} else if (device.equals(DEVICE_TOOL)) {
			return toolAction(type, id, action, parsedParams);
		}

		return failure(type, id, "Unknown device " + device);
	}

	private boolean mouseAction(String type, long id, final String action, final List<Object> parsedParams) throws InterruptedException {
		final List<Integer> params = toIntegerParams(parsedParams);
		if (params == null) {
			return false;
		}

		if (action.equals("press")) {
			if (params.size() == 1) {
				core.mouse().press(params.get(0));
			} else {
				return failure(type, id, "Unable to press mouse with " + params.size() + " parameters.");
			}
			return success(type, id);
		} else if (action.equals("release")) {
			if (params.size() == 1) {
				core.mouse().release(params.get(0));
			} else {
				return failure(type, id, "Unable to press mouse with " + params.size() + " parameters.");
			}
			return success(type, id);
		} else if (action.equals("left_click")) {
			if (params.isEmpty()) {
				core.mouse().leftClick();
			} else if (params.size() == 1) {
				core.mouse().leftClick(params.get(0));
			} else if (params.size() == 2) {
				core.mouse().leftClick(params.get(0), params.get(1));
			} else {
				return failure(type, id, "Unable to left click with " + params.size() + " parameters.");
			}
			return success(type, id);
		} else if (action.equals("right_click")) {
			if (params.isEmpty()) {
				core.mouse().rightClick();
			} else if (params.size() == 1) {
				core.mouse().rightClick(params.get(0));
			} else if (params.size() == 2) {
				core.mouse().rightClick(params.get(0), params.get(1));
			} else {
				return failure(type, id, "Unable to right click with " + params.size() + " parameters.");
			}
			return success(type, id);
		} else if (action.equals("move")) {
			if (params.size() == 2) {
				core.mouse().move(params.get(0), params.get(1));
			} else {
				return failure(type, id, "Unable to move mouse with " + params.size() + " parameters.");
			}
			return success(type, id);
		} else if (action.equals("move_by")) {
			if (params.size() == 2) {
				core.mouse().moveBy(params.get(0), params.get(1));
			} else {
				return failure(type, id, "Unable to move mouse by with " + params.size() + " parameters.");
			}
			return success(type, id);
		} else if (action.equals("drag")) {
			if (params.size() == 2) {
				Point p = core.mouse().getPosition();
				core.mouse().drag(p.x, p.y, params.get(0), params.get(1));
			} else if (params.size() == 4) {
				core.mouse().drag(params.get(0), params.get(1), params.get(2), params.get(3));
			} else {
				return failure(type, id, "Unable to drag mouse by with " + params.size() + " parameters.");
			}
			return success(type, id);
		} else if (action.equals("drag_by")) {
			if (params.size() == 2) {
				core.mouse().dragBy(params.get(0), params.get(1));
			} else {
				return failure(type, id, "Unable to drag mouse by with " + params.size() + " parameters.");
			}
			return success(type, id);
		} else if (action.equals("get_position")) {
			Point p = core.mouse().getPosition();
			return success(type, id, JsonNodeFactories.array(JsonNodeFactories.number(p.x), JsonNodeFactories.number(p.y)));
		} else if (action.equals("get_color")) {
			Point p = null;
			if (params.size() == 0) {
				p = core.mouse().getPosition();
			} else if (params.size() == 2) {
				p = new Point(params.get(0), params.get(1));
			}
			Color color = core.mouse().getColor(p);

			return success(type, id, JsonNodeFactories.array(	JsonNodeFactories.number(color.getRed()),
																JsonNodeFactories.number(color.getGreen()),
																JsonNodeFactories.number(color.getBlue())));
		} else {
			return unsupportedAction(type, id, action);
		}
	}

	private boolean keyboardAction(String type, long id, final String action, final List<Object> parsedParams) throws InterruptedException {
		if (action.equals("press")) {
			final List<Integer> params = toIntegerParams(parsedParams);
			if (params == null) {
				return false;
			}

			if (params.size() == 1) {
				core.keyBoard().press(params.get(0));
			} else {
				return failure(type, id, "Unable to press key with " + params.size() + " parameters.");
			}
			return success(type, id);
		} else if (action.equals("release")) {
			final List<Integer> params = toIntegerParams(parsedParams);
			if (params == null) {
				return false;
			}

			if (params.size() == 1) {
				core.keyBoard().release(params.get(0));
			} else {
				return failure(type, id, "Unable to release key with " + params.size() + " parameters.");
			}
			return success(type, id);
		} else if (action.equals("type")) {
			final List<Integer> params = toIntegerParams(parsedParams);
			if (params == null) {
				return false;
			}
			final int[] keys = IterableUtility.toIntegerArray(params);
			core.keyBoard().type(keys);
			return success(type, id);
		} else if (action.equals("type_string")) {
			final List<String> params = toStringParams(parsedParams);
			if (params == null) {
				return false;
			}
			final String[] strings = params.toArray(new String[]{});

			core.keyBoard().type(strings);
			return success(type, id);
		} else if (action.equals("combination")) {
			final List<Integer> params = toIntegerParams(parsedParams);
			if (params == null) {
				return false;
			}
			final int[] strings = IterableUtility.toIntegerArray(params);

			core.keyBoard().combination(strings);
			return success(type, id);
		}

		return unsupportedAction(type, id, action);
	}

	private boolean toolAction(String type, long id, final String action, final List<Object> parsedParams) throws InterruptedException {
		if (action.equals("get_clipboard")) {
			return success(type, id, Tools.getClipboard());
		} else if (action.equals("set_clipboard")) {
			List<String> params = toStringParams(parsedParams);
			if (params == null) {
				return false;
			}

			String data = params.get(0);
			Tools.setClipboard(data);
			return success(type, id);
		} else if (action.equals("execute")) {
			List<String> params = toStringParams(parsedParams);
			if (params == null) {
				return false;
			}

			if (params.size() == 1) {
				return success(type, id, Tools.execute(params.get(0)));
			} else if (params.size() == 2) {
				return success(type, id, Tools.execute(params.get(0), params.get(1)));
			} else {
				return failure(type, id, "Unexpected number of parameter for execution");
			}
		} else if (action.equals("get_selection")) {
			if (parsedParams.size() < 3) {
				return failure(type, id, "Need at least 3 parameters to get selection (title, selected, choices)");
			}

			Iterator<Object> it = parsedParams.iterator();
			String title = "";
			int selected = 0;
			try {
				title = (String) it.next();
				selected = (int) it.next();
			} catch (ClassCastException e) {
				return failure(type, id, "Need type string, int as first two paramters to get_selection");
			}

			List<Object> choiceObjects = new ArrayList<>();
			while (it.hasNext()) {
				choiceObjects.add(it.next());
			}
			final List<String> choices = toStringParams(choiceObjects);
			if (choices == null) {
				return failure(type, id, "Type of choices must be string");
			}
			int selection = SwingUtil.DialogUtil.getSelection(null, title, choices.toArray(new String[choices.size()]), selected);
			return success(type, id, JsonNodeFactories.number(selection));
		}

		return unsupportedAction(type, id, action);
	}

	private boolean unsupportedAction(String type, long id, final String action) {
		return failure(type, id, "Unsupported action " + action);
	}

	private List<String> toStringParams(List<Object> params) {
		List<String> output = new ArrayList<>();

		for (Object o : params) {
			if (o instanceof String) {
				output.add((String) o);
			} else {
				getLogger().warning("Unable to convert parameters. Not a string " + o);
				return null;
			}
		}

		return output;
	}

	private List<Integer> toIntegerParams(List<Object> params) {
		List<Integer> output = new ArrayList<>();

		for (Object o : params) {
			if (o instanceof Integer) {
				output.add((Integer) o);
			} else {
				getLogger().warning("Unable to convert parameters. Not an integer " + o);
				return null;
			}
		}

		return output;
	}

	private List<Object> parseParams(List<JsonNode> jsonParams) {
		List<Object> parsedParams = new LinkedList<>();
		if (jsonParams == null) {
			return parsedParams;
		}

		for (JsonNode param : jsonParams) {
			if (param.isNumberValue()) {
				Integer value = Integer.parseInt(param.getNumberValue());
				parsedParams.add(value);
			} else if (param.isStringValue()) {
				String value = param.getStringValue();
				parsedParams.add(value);
			}
		}
		return parsedParams;
	}

	@Override
	protected boolean verifyMessageContent(JsonNode content) {
		return content.isStringValue("device") &&
				(content.getStringValue("device").equals("mouse") ||
					content.getStringValue("device").equals("keyboard")) &&
				content.isStringValue("action") &&
				content.isArrayNode("parameters");
	}
}
