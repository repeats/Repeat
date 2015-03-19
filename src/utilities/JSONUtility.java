package utilities;

import java.io.File;

import argo.format.JsonFormatter;
import argo.format.PrettyJsonFormatter;
import argo.jdom.JdomParser;
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
	 * Read a JSON file and return a JSON object
	 *
	 * @param file
	 *            the file that will be read
	 * @return the root node of the JSON object
	 */
	public static JsonRootNode readJSON(File file) {
		StringBuffer strings = FileUtility.readFromFile(file);
		try {
			return new JdomParser().parse(strings.toString());
		} catch (InvalidSyntaxException e) {
			return null;
		}
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
	 * Private constructor so that no instance is created
	 */
	private JSONUtility() {}
}
