package core.ipc.repeatServer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import utilities.IterableUtility;
import argo.jdom.JsonNode;
import core.controller.Core;

class ControllerRequestProcessor extends AbstractMessageProcessor {

	private static final String DEVICE_MOUSE = "mouse";
	private static final String DEVICE_KEYBOARD = "keyboard";

	private final Core core;

	protected ControllerRequestProcessor(MainMessageSender messageSender, Core core) {
		super(messageSender);
		this.core = core;
	}

	@Override
	protected boolean process(String type, long id, JsonNode content) throws InterruptedException {
		final String device = content.getStringValue("device");
		String action = content.getStringValue("action");

		List<Object> parsedParams = parseParams(content.getArrayNode("params"));

		if (device.equals(DEVICE_MOUSE)) {
			return mouseAction(type, id, action, parsedParams);
		} else if (device.equals(DEVICE_KEYBOARD)) {
			return keyboardAction(type, id, action, parsedParams);
		}

		return failure(type, id, "Unknown device " + device);
	}

	private boolean mouseAction(String type, long id, final String action, final List<Object> parsedParams) throws InterruptedException {
		final List<Integer> params = toIntegerParams(parsedParams);
		if (params == null) {
			return false;
		}

		if (action.equals("left_click")) {
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
		} else {
			return unsupportedAction(type, id, action);
		}
	}

	private boolean keyboardAction(String type, long id, final String action, final List<Object> parsedParams) throws InterruptedException {
		if (action.equals("type")) {
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
				content.isArrayNode("params");
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(ControllerRequestProcessor.class.getName());
	}
}
