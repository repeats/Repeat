package core.userDefinedTask;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Shared variables used to pass values between tasks. This only supports string values since the tasks can be
 * written in different programming languages.
 */
public class SharedVariables {

	private static final Logger LOGGER = Logger.getLogger(SharedVariables.class.getName());

	public static final String GLOBAL_NAMESPACE = "global";
	private static final Map<String, Map<String, String>> variables = new HashMap<>();

	private final String namespace;

	public SharedVariables(String namespace) {
		this.namespace = namespace;
	}

	/**
	 * Get a value of a variable for the current namespace.
	 *
	 * @param variable name of the variable.
	 * @return value of the variable, or null if not exist.
	 */
	public String getVar(String variable) {
		return getVar(namespace, variable);
	}

	/**
	 * Set the value for a variable in the current namespace.
	 *
	 * @param variable variable name.
	 * @param value value of the variable.
	 * @return the existing value of the variable, or null if the variable does not exist before.
	 */
	public String setVar(String variable, String value) {
		return setVar(namespace, variable, value);
	}

	/**
	 * Delete the value for a variable in the current namespace.
	 *
	 * @param variable variable name.
	 * @return the existing value of the variable, or null if the variable does not exist before.
	 */
	public String delVar(String variable) {
		return delVar(namespace, variable);
	}

	/**
	 * Retrieve a variable value given namespace and variable name.
	 *
	 * @param namespace namespace where this variable belongs.
	 * @param variable name of the variable.
	 * @return value of the variable
	 */
	public static synchronized String getVar(String namespace, String variable) {
		Map<String, String> namespaceVariables = variables.get(namespace);
		if (namespaceVariables == null) {
			return null;
		}

		return namespaceVariables.get(variable);
	}

	/**
	 * Set the value for a variable in a namespace.
	 *
	 * @param namespace namespace where the variable belongs.
	 * @param variable variable name.
	 * @param value value of the variable.
	 * @return the existing value of the variable, or null if the variable does not exist before.
	 */
	public static synchronized String setVar(String namespace, String variable, String value) {
		if (isNullValue(namespace, "namespace") || isNullValue(variable, "variable") || isNullValue(value, "value")) {
			return null;
		}

		if (!variables.containsKey(namespace)) {
			variables.put(namespace, new HashMap<String, String>());
		}

		Map<String, String> namespaceVariables = variables.get(namespace);
		return namespaceVariables.put(variable, value);
	}

	/**
	 * Delete the value for a variable in a namespace.
	 *
	 * @param namespace namespace where the variable belongs.
	 * @param variable variable name.
	 * @return the existing value of the variable, or null if the variable does not exist before.
	 */
	public static synchronized String delVar(String namespace, String variable) {
		if (isNullValue(namespace, "namespace") || isNullValue(variable, "variable")) {
			return null;
		}

		if (!variables.containsKey(namespace)) {
			return null;
		}

		Map<String, String> namespaceVariables = variables.get(namespace);
		String result = namespaceVariables.remove(variable);

		if (namespaceVariables.isEmpty()) {
			variables.remove(namespace);
		}

		return result;
	}

	/**
	 * Simply check if the value is null or not and log a warning message if it is null.
	 *
	 * @param value the value to check.
	 * @param valueMeaning meaning of the value.
	 * @return if the value is null.
	 */
	private static boolean isNullValue(String value, String valueMeaning) {
		if (value == null) {
			LOGGER.warning("Setting null " + valueMeaning + "!");
			return true;
		}

		return false;
	}
}
