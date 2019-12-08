package core.userDefinedTask;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

/**
 * Shared variables used to pass values between tasks. This only supports string values since the tasks can be
 * written in different programming languages.
 */
public class SharedVariables {

	private static final Logger LOGGER = Logger.getLogger(SharedVariables.class.getName());

	public static final String GLOBAL_NAMESPACE = "global";
	private static final Map<String, Map<String, String>> variables = new HashMap<>();

	private static final Lock waiterLock = new ReentrantLock(true);
	private static final Map<SharedVariableIdentifier, List<Semaphore>> waiters = new HashMap<>();

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
	 * Wait for the next call to set the value of a variable.
	 *
	 * @return the new value of the variable, or null if timeout.
	 */
	public String waitVar(String variable, long timeoutMs) {
		return waitVar(namespace, variable, timeoutMs);
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
		String output = namespaceVariables.put(variable, value);

		SharedVariableIdentifier identifier = SharedVariableIdentifier.of(namespace, variable);
		waiterLock.lock();
		try {
			if (waiters.containsKey(identifier)) {
				List<Semaphore> locks = waiters.get(identifier);
				for (Semaphore waiter : locks) {
					waiter.release();
				}
				locks.clear();
			}
		} finally {
			waiterLock.unlock();
		}

		return output;
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
	 * Wait for the next call to set value of a variable with a timeout in milliseconds.
	 * This will wait until the variable value is set, or timeout occurs.
	 *
	 * @return the new value of the variable, or null if timeout.
	 */
	public static String waitVar(String namespace, String variable, long timeoutMs) {
		Semaphore s = new Semaphore(0);

		SharedVariableIdentifier identifier = SharedVariableIdentifier.of(namespace, variable);
		waiterLock.lock();
		try {
			if (!waiters.containsKey(identifier)) {
				waiters.put(identifier, new LinkedList<>());
			}

			waiters.get(identifier).add(s);
		} finally {
			waiterLock.unlock();
		}

		try {
			if (!s.tryAcquire(timeoutMs, TimeUnit.MILLISECONDS)) {
				return null;
			}
		} catch (InterruptedException e) {
			LOGGER.warning("Interrupted while waiting for semaphore.");
			return null;
		}

		return getVar(namespace, variable);
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

	private static class SharedVariableIdentifier {
		private String namespace;
		private String name;

		private SharedVariableIdentifier(String namespace, String name) {
			this.namespace = namespace;
			this.name = name;
		}

		private static SharedVariableIdentifier of(String namespace, String name) {
			return new SharedVariableIdentifier(namespace, name);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			SharedVariableIdentifier other = (SharedVariableIdentifier) obj;
			if (name == null) {
				if (other.name != null) {
					return false;
				}
			} else if (!name.equals(other.name)) {
				return false;
			}
			if (namespace == null) {
				if (other.namespace != null) {
					return false;
				}
			} else if (!namespace.equals(other.namespace)) {
				return false;
			}
			return true;
		}
	}
}
