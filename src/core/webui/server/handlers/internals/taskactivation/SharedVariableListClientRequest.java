package core.webui.server.handlers.internals.taskactivation;

import java.util.List;

import utilities.json.AutoJsonable;

public class SharedVariableListClientRequest extends AutoJsonable {
	private List<SharedVariableClientRequest> vars;

	public List<SharedVariableClientRequest> getVars() {
		return vars;
	}
}
