package core.ipc.repeatServer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import utilities.ILoggable;
import utilities.json.JSONUtility;

/**
 * This class sends messages to the ipc client
 * A generic message will have the following JSON format:
 * {
 * 		"id" : id of the message as integer,
 * 		"type" : type of the message as string. See {@link core.ipc.repeatServer.processors.ServerMainProcessor}
 * 		"content" : JSON content of the message (determined by upper layer)
 * }
 *
 * @author HP Truong
 *
 */
public class MainMessageSender implements ILoggable {

	private long idCount;
	private DataOutputStream writer;

	protected MainMessageSender() {
		idCount = 1L;
	}

	public synchronized long sendMessage(String type, JsonNode content) {
		long id = newID();
		if (sendMessage(type, id, content)) {
			return id;
		} else {
			return -1;
		}
	}

	public synchronized boolean sendMessage(String type, long id, JsonNode content) {
		JsonRootNode toSend = getMessage(type, id, content);

		synchronized (this) {
			try {
				writer.write(ClientServingThread.MESSAGE_DELIMITER);
				writer.write(ClientServingThread.MESSAGE_DELIMITER);
				writer.write(encode(JSONUtility.jsonToString(toSend)));
				writer.write(ClientServingThread.MESSAGE_DELIMITER);
				writer.write(ClientServingThread.MESSAGE_DELIMITER);
				writer.flush();
			} catch (IOException e) {
				getLogger().log(Level.WARNING, "Exception while writing message", e);
				return false;
			}
		}

		return true;
	}

	private JsonRootNode getMessage(String type, long id, JsonNode message) {
		return JsonNodeFactories.object(
				JsonNodeFactories.field("type", JsonNodeFactories.string(type)),
				JsonNodeFactories.field("id", JsonNodeFactories.number(id)),
				JsonNodeFactories.field("content", message)
				);
	}

	private synchronized long newID() {
		idCount++;
		return idCount;
	}

	protected void setWriter(DataOutputStream writer) {
		this.writer = writer;
	}

	/**
	 * Encode a message to send from server to client.
	 *
	 * @param message message to encode.
	 * @return byte array representing bytes to send to client.
	 */
	private byte[] encode(String message) {
		return Base64.getEncoder().encode(message.getBytes(ControllerServer.ENCODING));
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(MainMessageSender.class.getName());
	}
}
