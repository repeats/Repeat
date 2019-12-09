package core.userDefinedTask.internals;

public class SharedVariablesEvent {
	private String namespace;
	private String name;

	private SharedVariablesEvent(String namespace, String name) {
		this.namespace = namespace;
		this.name = name;
	}

	public static SharedVariablesEvent of(String namespace, String name) {
		return new SharedVariablesEvent(namespace, name);
	}

	public String getNamespace() {
		return namespace;
	}

	public String getName() {
		return name;
	}
}
