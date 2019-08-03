package core.ipc.repeatClient.repeatPeerClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import utilities.json.IJsonable;
import utilities.json.JSONUtility;

public class RepeatsPeerServiceClientManager implements IJsonable {

	private static final Logger LOGGER = Logger.getLogger(RepeatsPeerServiceClientManager.class.getName());

	private Map<String, RepeatsPeerServiceClient> clients;

	public RepeatsPeerServiceClientManager() {
		this.clients = new HashMap<>();
	}

	public Collection<RepeatsPeerServiceClient> getClients() {
		return clients.values();
	}

	/**
	 * Starts all clients.
	 *
	 * @param onStartup
	 *            this is a call on startup, only start clients that should be
	 *            launched at startup.
	 */
	public void startAllClients(boolean onStartup) throws IOException {
		for (RepeatsPeerServiceClient client : clients.values()) {
			if (!onStartup || client.isLaunchAtStartup()) {
				client.startRunning();
			}
		}
	}

	public void startClient(String id) {
		if (!clients.containsKey(id)) {
			LOGGER.log(Level.WARNING, "No client with ID " + id + " found.");
			return;
		}

		RepeatsPeerServiceClient client = clients.get(id);
		try {
			client.startRunning();
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Exception when starting client.", e);
		}
	}

	public void addAndStartClient(String host, int port) {
		RepeatsPeerServiceClient client = new RepeatsPeerServiceClient(host, port);
		try {
			client.startRunning();
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Failed to start client running at " + host + ":" + port + ".", e);
		}
		clients.put(client.getClientId(), client);
	}

	public boolean stopClient(String id) {
		if (!clients.containsKey(id)) {
			LOGGER.log(Level.WARNING, "No client with ID " + id + " found.");
			return false;
		}

		RepeatsPeerServiceClient client = clients.get(id);
		try {
			client.stopRunning();
			return true;
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Exception when stopping client.", e);
			return false;
		}
	}

	public void stopAndRemoveClient(String id) {
		if (stopClient(id)) {
			clients.remove(id);
		}
	}

	public void stopAllClients() throws IOException {
		for (RepeatsPeerServiceClient client : clients.values()) {
			client.stopRunning();
		}
	}

	public void setLaunchAtStartup(String id, boolean value) {
		if (!clients.containsKey(id)) {
			LOGGER.log(Level.WARNING, "No client with ID " + id + " found.");
			return;
		}

		clients.get(id).setLaunchAtStartup(value);
	}

	@Override
	public JsonRootNode jsonize() {
		return JsonNodeFactories.object(
				JsonNodeFactories.field(
						JsonNodeFactories.string("clients"),
						JsonNodeFactories.array(JSONUtility.listToJson(clients.values()))));
	}

	public static RepeatsPeerServiceClientManager parseJSON(JsonNode node) {
		List<RepeatsPeerServiceClient> clients = new ArrayList<>();
		List<JsonNode> clientNodes = node.getArrayNode("clients");
		for (JsonNode clientNode : clientNodes) {
			RepeatsPeerServiceClient client = RepeatsPeerServiceClient.parseJSON(clientNode);
			clients.add(client);
		}
		RepeatsPeerServiceClientManager manager = new RepeatsPeerServiceClientManager();
		for (RepeatsPeerServiceClient client : clients) {
			manager.clients.put(client.getClientId(), client);
		}

		return manager;
	}
}
