package utilities.json;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import argo.format.JsonFormatter;
import argo.format.PrettyJsonFormatter;
import argo.jdom.JdomParser;
import argo.jdom.JsonField;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import argo.jdom.JsonStringNode;
import argo.saj.InvalidSyntaxException;
import utilities.FileUtility;
import utilities.Function;

public class JSONUtility {

	/**
	 * Convert JsonNode to string representation
	 * @param node JSON node
	 * @return string representation of the json node
	 */
	public static String jsonToString(JsonNode node) {
		return jsonToString(node.getRootNode());
	}

	/**
	 * Convert JsonRootnode to string representation
	 * @param node JSON root node
	 * @return string representation of the json node
	 */
	public static String jsonToString(JsonRootNode node) {
		JsonFormatter JSON_FORMATTER = new PrettyJsonFormatter();
		return JSON_FORMATTER.format(node);
	}

	/**
	 * Parse a json from a string
	 * @param input the string representing the JSON object
	 * @return the root node of the json object, or null if the string is of invalid syntax
	 */
	public static JsonRootNode jsonFromString(String input) {
		try {
			return new JdomParser().parse(input);
		} catch (InvalidSyntaxException e) {
			return null;
		}
	}

	/**
	 * Read a JSON file and return a JSON object
	 *
	 * @param file
	 *            the file that will be read
	 * @return the root node of the JSON object
	 */
	public static JsonRootNode readJSON(File file) {
		StringBuffer strings = FileUtility.readFromFile(file);
		return jsonFromString(strings.toString());
	}

	/**
	 * Write a JSON content into a file
	 * @param node the json node which content will be written to file
	 * @param file the file to write to. File content will be overriden. (i.e. no appending)
	 * @return return if write successfully
	 */
	public static boolean writeJson(JsonRootNode node, File file) {
		return FileUtility.writeToFile(new StringBuffer(JSONUtility.jsonToString(node)), file, false);
	}

	/**
	 * Convert a collection of jsonable objects to list of json objects
	 * @param collection collection of jsonable objects implementing <class> IJsonable </class>
	 * @return list of JSON objects converted from the input list. Underlying implementation is currently using LinkedList
	 */
	public static List<JsonNode> listToJson(Collection<? extends IJsonable> collection) {
		return collection.stream().map(IJsonable::jsonize).collect(Collectors.toList());
	}

	/**
	 * Convert an iterable of strings to list of json objects
	 * @param collection iterable of strings
	 * @return list of JSON objects converted from the input list. Underlying implementation is currently using LinkedList
	 */
	public static List<JsonNode> listToJson(Iterable<String> collection) {
		List<JsonNode> jsonList = new LinkedList<>();
		for (String item : collection) {
			jsonList.add(JsonNodeFactories.string(item));
		}

		return jsonList;
	}

	/**
	 * Construct a JSON object from a string to string map.
	 *
	 * @param map a string to string map.
	 * @return the corresponding JSON object.
	 */
	public static JsonNode stringMapToJson(Map<String, String> map) {
		List<JsonField> fields = new ArrayList<>();
		for (Entry<String, String> entry : map.entrySet()) {
			fields.add(JsonNodeFactories.field(entry.getKey(), JsonNodeFactories.string(entry.getValue())));
		}

		return JsonNodeFactories.object(fields);
	}

	/**
	 * Construct a string to string map from a JSON node.
	 * The node must be a JSON object.
	 *
	 * @param node JSON node object to construct the map.
	 * @return the corresponding string to string map.
	 */
	public static Map<String, String> jsonToStringMap(JsonNode node) {
		Map<String, String> result = new HashMap<>();
		for (Entry<JsonStringNode, JsonNode> entry : node.getObjectNode().entrySet()) {
			result.put(entry.getKey().getStringValue(), entry.getValue().getStringValue());
		}

		return result;
	}

	/**
	 * Attempt to parse JSON from a list, then add all results to an output collection. This does not add
	 * nodes that cannot be parsed (returning null)
	 * @param nodes input list of json nodes
	 * @param parser function to parse json node to the object
	 * @param output the output collection where parsed objects will be added to
	 * @return true if all JSON objects were successfully parsed, and false otherwise
	 */
	public static <E> boolean addAllJson(Collection<JsonNode> nodes, Function<JsonNode, E> parser, Collection<E> output) {
		boolean success = true;
		for (JsonNode node : nodes) {
			E parsed = parser.apply(node);
			success &= (parsed != null);
			if (success) {
				output.add(parsed);
			}
		}

		return success;
	}

	/**
	 * Add a child to the current json node
	 * @param original the original json node where the new child will be added
	 * @param key the key of the child that will be added
	 * @param value json node value of the added child
	 * @return the json node with the added child, or null if operation failed
	 */
	public static JsonNode addChild(JsonNode original, String key, JsonNode value) {
		Map<JsonStringNode, JsonNode> existingFields = original.getFields();
		Map<JsonStringNode, JsonNode> newMap = new HashMap<>();
		for (Entry<JsonStringNode, JsonNode> entry : existingFields.entrySet()) {
			newMap.put(entry.getKey(), entry.getValue());
		}

		newMap.put(JsonNodeFactories.string(key), value);
		return JsonNodeFactories.object(newMap);
	}

	/**
	 * Remove a child (or children) from the current json node given the key to remove
	 * @param original the original json node where the new child will be removed
	 * @param key the key of the child that will be removed
	 * @return the json node with the removed child, or null if operation failed
	 */
	public static JsonNode removeChild(JsonNode original, String key) {
		Map<JsonStringNode, JsonNode> existingFields = original.getFields();
		Map<JsonStringNode, JsonNode> newMap = new HashMap<>();
		for (Entry<JsonStringNode, JsonNode> entry : existingFields.entrySet()) {
			if (!entry.getKey().toString().equals(key)) {
				newMap.put(entry.getKey(), entry.getValue());
			}
		}

		return JsonNodeFactories.object(newMap);
	}

	/**
	 * Same as replace children but apply for only one child
	 * @param parent parent node
	 * @param replacingKey the key which value is replaced
	 * @param replacingChild the new child which will be in place of the old one
	 * @return an instance of JsonNode with the old child with specified replacingKey replaced
	 */
	public static JsonNode replaceChild(JsonNode parent, String replacingKey, JsonNode replacingChild) {
		Map<String, JsonNode> childMap = new HashMap<>();
		childMap.put(replacingKey, replacingChild);
		return replaceChildren(parent, childMap);
	}

	/**
	 * Replace a set of children in a JSON object.
	 * @param parent the parent node
	 * @param replacingChildren the map key: newNode to replace. Key must exist in parent node beforehand
	 * @return an instance of JsonNode with all children in replacing children replaced.
	 */
	public static JsonNode replaceChildren(JsonNode parent, Map<String, JsonNode> replacingChildren) {
		if (parent == null) {
			return null;
		}
		List<JsonField> fields = new ArrayList<JsonField>();

		for (JsonField field : parent.getFieldList()) {
			String key = field.getName().getText();

			if (parent.isNode(key)) { // Fail safe
				if (replacingChildren.containsKey(key)) {
					fields.add(JsonNodeFactories.field(key, replacingChildren.get(key)));
				} else {
					fields.add(field);
				}
			}
		}

		return JsonNodeFactories.object(fields);
	}

	/**
	 * Private constructor so that no instance is created
	 */
	private JSONUtility() {}
}
