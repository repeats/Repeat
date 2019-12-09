package core.userDefinedTask.internals;

public class SharedVariablesSubscriber {

	private boolean all;
	private boolean allForNamespace;
	private String namespace;
	private String name;
	private ProcessingFunction f;

	private SharedVariablesSubscriber(String namespace, String name, boolean all, boolean allForNamespace, ProcessingFunction f) {
		this.namespace = namespace;
		this.name = name;
		this.all = all;
		this.allForNamespace = allForNamespace;
		this.f = f;
	}

	public static SharedVariablesSubscriber forAll(ProcessingFunction f) {
		return new SharedVariablesSubscriber(null, null, true, true, f);
	}

	public static SharedVariablesSubscriber forNamespace(String namespace, ProcessingFunction f) {
		return new SharedVariablesSubscriber(namespace, null, false, true, f);
	}

	public static SharedVariablesSubscriber forVar(String namespace, String name, ProcessingFunction f) {
		return new SharedVariablesSubscriber(namespace, name, false, false, f);
	}

	public void processEvent(SharedVariablesEvent e) {
		if (!shouldProcess(e)) {
			return;
		}

		f.process(e);
	}

	private boolean shouldProcess(SharedVariablesEvent e) {
		if (all) {
			return true;
		}

		if (!e.getNamespace().equals(namespace)) {
			return false;
		}
		if (allForNamespace) {
			return true;
		}

		return e.getName().equals(name);
	}

	public static interface ProcessingFunction {
		public abstract void process(SharedVariablesEvent e);
	}
}
