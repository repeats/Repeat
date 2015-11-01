package core.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import utilities.ExceptableFunction;
import utilities.Function;
import utilities.JSONUtility;
import utilities.Trio;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;

import com.sun.istack.internal.logging.Logger;

import core.controller.Core;

public class RequestParser {

	private static final Logger LOGGER = Logger.getLogger(RequestParser.class);
	private static final Map<String, Function<Trio<Core, String, List<Object>>, ExceptableFunction<Void, Object, InterruptedException>>> deviceFunctions;

	static {
		deviceFunctions = new HashMap<>();
		deviceFunctions.put("mouse", new Function<Trio<Core, String, List<Object>>, ExceptableFunction<Void, Object, InterruptedException>>() {
			@Override
			public ExceptableFunction<Void, Object, InterruptedException> apply(Trio<Core, String, List<Object>> d) {
				final Core core = d.getA();
				final String action = d.getB();
				final List<Object> params = d.getC();
				final List<Integer> converetedParams = new ArrayList<>();
				if (params != null) {
					for (Object o : params) {
						if (o instanceof Integer) {
							converetedParams.add((Integer) o);
						} else {
							LOGGER.warning("Unable to convert mouse parameters. Not an integer " + o);
							return null;
						}
					}
				}

				switch (action) {
				case "leftClick":
					return new ExceptableFunction<Void, Object, InterruptedException>() {
						@Override
						public Object apply(Void d) throws InterruptedException {
							if (converetedParams.isEmpty()) {
								core.mouse().leftClick();
							} else if (converetedParams.size() == 1) {
								core.mouse().leftClick(converetedParams.get(0));
							} else if (converetedParams.size() == 2) {
								core.mouse().leftClick(converetedParams.get(0), converetedParams.get(1));
							} else {
								LOGGER.warning("Unable to left click with " + converetedParams.size() + " parameters.");
							}
							return null;
						}
					};
				case "rightClick":
					return new ExceptableFunction<Void, Object, InterruptedException>() {
						@Override
						public Object apply(Void d) throws InterruptedException {
							if (converetedParams.isEmpty()) {
								core.mouse().rightClick();
							} else if (converetedParams.size() == 1) {
								core.mouse().rightClick(converetedParams.get(0));
							} else if (converetedParams.size() == 2) {
								core.mouse().rightClick(converetedParams.get(0), converetedParams.get(1));
							} else {
								LOGGER.warning("Unable to right click with " + converetedParams.size() + " parameters.");
							}
							return null;
						}
					};
				case "move":
					return new ExceptableFunction<Void, Object, InterruptedException>() {
						@Override
						public Object apply(Void d) throws InterruptedException {
							if (converetedParams.size() == 2) {
								core.mouse().move(converetedParams.get(0), converetedParams.get(1));
							} else {
								LOGGER.warning("Unable to move mouse with " + converetedParams.size() + " parameters.");
							}
							return null;
						}
					};
				case "moveBy":
					return new ExceptableFunction<Void, Object, InterruptedException>() {
						@Override
						public Object apply(Void d) {
							if (converetedParams.size() == 2) {
								core.mouse().moveBy(converetedParams.get(0), converetedParams.get(1));
							} else {
								LOGGER.warning("Unable to move mouse by with " + converetedParams.size() + " parameters.");
							}
							return null;
						}
					};

				default:
					return null;
				}
			}
		});

		deviceFunctions.put("keyboard", new Function<Trio<Core, String, List<Object>>, ExceptableFunction<Void, Object, InterruptedException>>() {
			@Override
			public ExceptableFunction<Void, Object, InterruptedException> apply(Trio<Core, String, List<Object>> d) {
				final Core core = d.getA();
				final String action = d.getB();
				final List<Object> params = d.getC();

				switch (action) {
				case "type":
					return new ExceptableFunction<Void, Object, InterruptedException>() {
						@Override
						public Object apply(Void d) throws InterruptedException {
							int[] keys = new int[params.size()];
							for (ListIterator<Object> iterator = params.listIterator(); iterator.hasNext();) {
								int i = iterator.nextIndex();
								Object o = iterator.next();
								if (o instanceof Integer) {
									keys[i] = (int) o;
								} else {
									LOGGER.warning("Unable to type with invalid non-integer parameter " + o);
									return null;
								}
							}

							core.keyBoard().type(keys);
							return null;
						}
					};

				case "typeString":
					return new ExceptableFunction<Void, Object, InterruptedException>() {
						@Override
						public Object apply(Void d) {
							String[] strings = new String[params.size()];
							for (ListIterator<Object> iterator = params.listIterator(); iterator.hasNext();) {
								int i = iterator.nextIndex();
								Object o = iterator.next();
								if (o instanceof String) {
									strings[i] = (String) o;
								} else {
									LOGGER.warning("Unable to type string with invalid non-string parameter " + o);
									return null;
								}
							}

							core.keyBoard().type(strings);

							return null;
						}
					};

				case "combination":
					return new ExceptableFunction<Void, Object, InterruptedException>() {
						@Override
						public Object apply(Void d) {
							int[] keys = new int[params.size()];
							for (ListIterator<Object> iterator = params.listIterator(); iterator.hasNext();) {
								int i = iterator.nextIndex();
								Object o = iterator.next();
								if (o instanceof Integer) {
									keys[i] = (int) o;
								} else {
									LOGGER.warning("Unable to type combination with invalid non-integer parameter " + o);
									return null;
								}
							}

							core.keyBoard().combination(keys);
							return null;
						}
					};

				default:
					break;
				}

				return null;
			}
		});

		/**
		 * System message: configuring the system
		 */
		deviceFunctions.put("system", new Function<Trio<Core, String, List<Object>>, ExceptableFunction<Void, Object, InterruptedException>>() {
			@Override
			public ExceptableFunction<Void, Object, InterruptedException> apply(Trio<Core, String, List<Object>> d) {
//				final Core core = d.getA(); //Unused
				final String action = d.getB();
//				final List<Object> params = d.getC(); //Unused

				switch (action) {
				case "keepAlive":
					return new ExceptableFunction<Void, Object, InterruptedException>() {
						@Override
						public Object apply(Void d) throws InterruptedException {
							//Null function, intent is to keep connection alive
							return null;
						}
					};
				default:
					break;
				}

				return null;
			}
		});
	}

	/**
	 * Parse a request from client. Sample request:
	 * {
	 * 		"actions" : [
	 * 			{
	 * 				"device" : "mouse",
	 * 				"action" : "move",
	 * 				"params" : [124, 125]
	 * 			},
	 *			{
	 * 				"device" : "mouse",
	 * 				"action" : "leftClick",
	 * 				"params" : null
	 * 			},
	 * 			{
	 *				"device" : "keyboard",
	 * 				"action" : "typeKey",
	 * 				"params" : [1, 2, 3, 4, 5]
	 * 			},
	 * 			{
	 *				"device" : "keyboard",
	 * 				"action" : "typeString",
	 * 				"params" : ["aaa"]
	 * 			}
	 * 		]
	 * }
	 * @param request request from client as JSON string
	 * @param core Core controller that will execute the action
	 * @return list of actions need to perform in order
	 */
	protected static List<ExceptableFunction<Void, Object, InterruptedException>> parseRequest(String request, Core core) {
		List<ExceptableFunction<Void, Object, InterruptedException>> output = new LinkedList<>();

		LOGGER.info("Parsing request: " + request);
		JsonRootNode root = JSONUtility.jsonFromString(request);
		if (root == null) {
			return output;
		}

		List<JsonNode> actions = root.getNullableArrayNode("actions");
		if (actions == null) {
			return output;
		}

		for (JsonNode action : actions) {
			ExceptableFunction<Void, Object, InterruptedException> toAdd = parseAction(action, core);
			if (toAdd != null) {
				output.add(toAdd);
			}
		}

		return output;
	}

	/**
	 * Parse a single action from action request.
	 * See parseRequest function for all possible actions
	 * @param action JSON node describing the action
	 * @param core Core controller that will execute the action
	 * @return a function representing the action
	 */
	private static ExceptableFunction<Void, Object, InterruptedException> parseAction(JsonNode action, Core core) {
		String device = action.getStringValue("device");
		String toDo = action.getStringValue("action");

		List<JsonNode> params = action.getNullableArrayNode("params");
		List<Object> parsedParams = new LinkedList<>();

		for (JsonNode param : params) {
			if (param.isNumberValue()) {
				Integer value = Integer.parseInt(param.getNumberValue());
				parsedParams.add(value);
			} else if (param.isStringValue()) {
				String value = param.getStringValue();
				parsedParams.add(value);
			}
		}

		return deviceFunctions.get(device).apply(new Trio<Core, String, List<Object>>(core, toDo, parsedParams));
	}

	private RequestParser() {
		throw new UnsupportedOperationException("Cannot instantiate instance of this class");
	}
}
