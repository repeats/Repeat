package core.ipc.repeatClient.repeatPeerClient;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.ipc.ApiProtocol;
import core.ipc.IPCProtocol;
import core.ipc.repeatClient.repeatPeerClient.ResponseManager.Reply;
import core.ipc.repeatServer.ControllerServer;
import core.ipc.repeatServer.processors.IpcMessageType;
import utilities.json.IJsonable;
import utilities.json.JSONUtility;

public class RepeatPeerServiceClientWriter extends AbstractRepeatsClientStoppableThread {

	private static final long REPLY_WAIT_TIMEOUT_MS = 10000;
	private static final long MESSAGE_WAIT_TIMEOUT_MS = (long) (ControllerServer.DEFAULT_TIMEOUT_MS * 0.8);

	private DataOutputStream writer;
	private long currentId;
	private LinkedBlockingQueue<String> messageQueue;

	protected RepeatPeerServiceClientWriter(DataOutputStream writer, ResponseManager responseManager) {
		super(responseManager);
		this.writer = writer;
		this.messageQueue = new LinkedBlockingQueue<>();
	}

	@Override
	protected void processLoop() throws IOException, InterruptedException {
		String message = messageQueue.poll(MESSAGE_WAIT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
		if (message == null) {
			enqueueKeepAlive();
			return;
		}

		IPCProtocol.sendMessage(writer, message);
	}

	public long enqueueMessage(IpcMessageType type, IJsonable message) {
		return enqueueMessage(type.toString(), message);
	}

	public long enqueueMessage(String type, IJsonable message) {
		return enqueueMessage(type, message.jsonize());
	}

	private long enqueueMessage(IpcMessageType type, JsonNode message) {
		return enqueueMessage(type.toString(), message);
	}

	public long enqueueMessage(String type, JsonNode message) {
		long newId = 0L;
		synchronized (this) {
			newId = ++currentId;
		}
		JsonRootNode messageNode = JsonNodeFactories.object(
				JsonNodeFactories.field(JsonNodeFactories.string("id"), JsonNodeFactories.number(newId)),
				JsonNodeFactories.field(JsonNodeFactories.string("type"), JsonNodeFactories.string(type)),
				JsonNodeFactories.field(JsonNodeFactories.string("content"), message)
				);
		messageQueue.offer(JSONUtility.jsonToString(messageNode));
		return newId;
	}

	public Reply waitForReply(long id) throws InterruptedException {
		return responseManager.waitFor(id, REPLY_WAIT_TIMEOUT_MS);
	}

	protected void enqueueKeepAlive() {
		enqueueMessage(IpcMessageType.SYSTEM_HOST, ApiProtocol.keepAliveMessage());
	}
}
