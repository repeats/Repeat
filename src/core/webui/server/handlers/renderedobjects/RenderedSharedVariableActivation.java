package core.webui.server.handlers.renderedobjects;

public class RenderedSharedVariableActivation {
	private String namespace;
	private String name;

	public static RenderedSharedVariableActivation of(String namespace, String name) {
		RenderedSharedVariableActivation output = new RenderedSharedVariableActivation();
		output.namespace = namespace;
		output.name = name;
		return output;
	}

	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
