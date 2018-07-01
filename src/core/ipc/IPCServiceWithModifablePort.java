package core.ipc;

import java.util.logging.Level;

import argo.jdom.JsonNode;
import core.ipc.repeatServer.ControllerServer;

/**
 * IPC service with modifiable port to start at. E.g. a server.
 */
public abstract class IPCServiceWithModifablePort extends IIPCService {

	@Override
	protected boolean extractSpecificConfig(JsonNode node) {
		boolean result = true;
		if (!super.extractSpecificConfig(node)) {
			getLogger().warning("Cannot parse parent config for " + ControllerServer.class);
			result = false;
		}

		// If port not specified then use default port.
		if (!node.isNumberValue("port")) {
			return result;
		}

		try {
			String portString = node.getNumberValue("port");
			int port = Integer.parseInt(portString);
			return result && setPort(port);
		} catch (NumberFormatException e) {
			getLogger().log(Level.WARNING, "Controller service port is not an integer.", e);
			return false;
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Cannot parse controller config.", e);
			return false;
		}
	}
}
