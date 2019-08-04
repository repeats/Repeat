package core.userDefinedTask.internals;

import java.io.File;
import java.util.logging.Logger;

import core.ipc.repeatClient.repeatPeerClient.RepeatsPeerServiceClient;
import core.ipc.repeatClient.repeatPeerClient.RepeatsPeerServiceClientManager;

public class RemoteRepeatsClientTools implements ITools {

	private static final Logger LOGGER = Logger.getLogger(RemoteRepeatsClientTools.class.getName());

	private String clientId;
	private RepeatsPeerServiceClientManager clientManager;

	public RemoteRepeatsClientTools(RepeatsPeerServiceClientManager clientManager, String clientId) {
		this.clientId = clientId;
		this.clientManager = clientManager;
	}

	private RepeatsPeerServiceClient getClient() {
		return clientManager.getClient(clientId);
	}

	@Override
	public String getClipboard() {
		RepeatsPeerServiceClient client = getClient();
		if (client == null) {
			return "";
		}

		if (!client.isRunning()) {
			LOGGER.info("Client " + client.getName() + " is not running.");
			return "";
		}
		return client.api().tool().getClipboard();
	}

	@Override
	public boolean setClipboard(String data) {
		RepeatsPeerServiceClient client = getClient();
		if (client == null) {
			return false;
		}

		if (!client.isRunning()) {
			LOGGER.info("Client " + client.getName() + " is not running.");
			return false;
		}
		try {
			client.api().tool().setClipboard(data);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public String execute(String command) {
		RepeatsPeerServiceClient client = getClient();
		if (client == null) {
			return "";
		}

		if (!client.isRunning()) {
			LOGGER.info("Client " + client.getName() + " is not running.");
			return "";
		}
		return client.api().tool().execute(command);
	}

	@Override
	public String execute(String command, File cwd) {
		RepeatsPeerServiceClient client = getClient();
		if (client == null) {
			return "";
		}

		if (!client.isRunning()) {
			LOGGER.info("Client " + client.getName() + " is not running.");
			return "";
		}
		return client.api().tool().execute(command, cwd.getPath());
	}
}
