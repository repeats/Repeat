package core.ipc.repeatServer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import utilities.ILoggable;
import utilities.JSONUtility;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;

public class MainMessageSender implements ILoggable {

	private long idCount;
	private BufferedWriter writer;

	protected MainMessageSender() {
		idCount = 1L;
	}

	public long sendMessage(String type, JsonNode content) {
		long id = newID();
		if (sendMessage(type, id, content)) {
			return id;
		} else {
			return -1;
		}
	}

	public boolean sendMessage(String type, long id, JsonNode content) {
		JsonRootNode toSend = getMessage(type, id, content);

		synchronized (this) {
			try {
				writer.write(JSONUtility.jsonToString(toSend));
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

	protected void setWriter(BufferedWriter writer) {
		getLogger().info("Set to be " + writer);
		this.writer = writer;
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(MainMessageSender.class.getName());
	}
}
