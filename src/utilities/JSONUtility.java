package utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import argo.format.JsonFormatter;
import argo.format.PrettyJsonFormatter;
import argo.jdom.JdomParser;
import argo.jdom.JsonField;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import argo.saj.InvalidSyntaxException;

public class JSONUtility {

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
		List<JsonField> fields = new ArrayList<JsonField>();

		for (JsonField field : parent.getFieldList()) {
			String key = field.getName().getText();

			if (parent.isNode(key)) {//Fail safe
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
