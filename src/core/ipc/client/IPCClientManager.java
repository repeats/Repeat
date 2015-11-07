package core.ipc.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class IPCClientManager {

	private final Map<String, AbstractIPCClient> ipcServices;

	public IPCClientManager() {
		ipcServices = new HashMap<>();

		ipcServices.put("python", new PythonIPCClient());
	}

	public AbstractIPCClient getClient(String name) {
		return ipcServices.get(name);
	}

	public Collection<AbstractIPCClient> getAllClients() {
		return ipcServices.values();
	}
}
