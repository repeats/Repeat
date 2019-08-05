package core.config;

import java.util.ArrayList;
import java.util.List;

import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import utilities.json.IJsonable;
import utilities.json.JSONUtility;

public class AbstractRemoteRepeatsClientsConfig implements IJsonable {

	public static final String LOCAL_CLIENT = "local";

	private List<String> enabledClients;

	protected AbstractRemoteRepeatsClientsConfig(List<String> remoteClientIds) {
		this.enabledClients = new ArrayList<>(remoteClientIds);
	}

	public final List<String> getClients() {
		return enabledClients;
	}

	public final void setClients(List<String> remoteClientIds) {
		enabledClients.clear();
		enabledClients.addAll(remoteClientIds);
	}

	@Override
	public final JsonRootNode jsonize() {
		return JsonNodeFactories.array(JSONUtility.listToJson(enabledClients));
	}
}
