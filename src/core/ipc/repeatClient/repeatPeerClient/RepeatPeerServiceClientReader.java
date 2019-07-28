package core.ipc.repeatClient.repeatPeerClient;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.logging.Logger;

import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;
import core.ipc.IPCProtocol;
import core.ipc.repeatClient.repeatPeerClient.ResponseManager.Reply;
import utilities.json.JSONUtility;

class RepeatPeerServiceClientReader extends AbstractRepeatsClientStoppableThread {

	private static final Logger LOGGER = Logger.getLogger(RepeatPeerServiceClientReader.class.getName());

	private Reader reader;

	protected RepeatPeerServiceClientReader(Reader reader, ResponseManager responseManager) {
		super(responseManager);
		this.reader = reader;
	}

	@Override
	protected void processLoop() throws IOException, InterruptedException {
		List<String> messages = IPCProtocol.getMessages(reader);
		if (messages == null || messages.size() == 0) {
			LOGGER.warning("Messages is null or messages size is 0. " + messages);
			return;
		}

		for (String message : messages) {
			processMessage(message);
		}
	}

	private void processMessage(String message) throws InterruptedException {
		JsonRootNode root = JSONUtility.jsonFromString(message);
		if (root == null || !verifyMessage(root)) {
			LOGGER.warning("Invalid messaged received " + message);
		}

		LOGGER.fine("Receive " + message);
		String type = root.getStringValue("type");
		long id = Long.parseLong(root.getNumberValue("id"));
		JsonNode content = root.getNode("content");
		process(type, id, content);
	}

	public void process(String type, long id, JsonNode content) throws InterruptedException {
		String status = content.getStringValue("status");
		JsonNode message = content.getNode("message");
		responseManager.notifyFor(id, Reply.of(status, message));
	}

	private boolean verifyMessage(JsonRootNode node) {
		return node.isNumberValue("id")
				&& node.isStringValue("type")
				&& node.isObjectNode("content")
				&& node.getNode("content").isNullableBooleanValue("is_reply_message")
				&& node.getNode("content").getBooleanValue("is_reply_message");
	}
}
