package core.ipc.repeatClient.repeatPeerClient.api;

import java.util.logging.Level;
import java.util.logging.Logger;

import argo.jdom.JsonNode;
import core.ipc.ApiProtocol;
import core.ipc.repeatClient.repeatPeerClient.RepeatPeerServiceClientWriter;
import core.ipc.repeatClient.repeatPeerClient.ResponseManager.Reply;
import core.ipc.repeatServer.processors.IpcMessageType;

abstract class AbstractRepeatsClientApi {

	private static final Logger LOGGER = Logger.getLogger(AbstractRepeatsClientApi.class.getName());

	private RepeatPeerServiceClientWriter repeatPeerServiceClientWriter;

	protected AbstractRepeatsClientApi(RepeatPeerServiceClientWriter repeatPeerServiceClientWriter) {
		this.repeatPeerServiceClientWriter = repeatPeerServiceClientWriter;
	}

	protected RepeatPeerServiceClientWriter getRepeatPeerServiceClientWriter() {
		return repeatPeerServiceClientWriter;
	}

	protected final String waitAndGetResponseIfSuccess(IpcMessageType type, DeviceCommand deviceCommand) {
		JsonNode node = waitAndGetJsonResponseIfSuccess(type, deviceCommand);
		if (node == null) {
			return "";
		}
		return node.getStringValue();
	}

	protected final JsonNode waitAndGetJsonResponseIfSuccess(IpcMessageType type, DeviceCommand deviceCommand) {
		long id = getRepeatPeerServiceClientWriter().enqueueMessage(IpcMessageType.ACTION, deviceCommand);
		Reply reply;
		try {
			reply = getRepeatPeerServiceClientWriter().waitForReply(id);
		} catch (InterruptedException e) {
			LOGGER.log(Level.WARNING, "Interrupted when running command", e);
			return null;
		}

		if (reply.getStatus().equals(ApiProtocol.SUCCESS_STATUS)) {
			return reply.getMessage();
		}
		return null;
	}
}
