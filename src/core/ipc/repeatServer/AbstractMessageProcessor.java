package core.ipc.repeatServer;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import core.ILoggable;

abstract class AbstractMessageProcessor implements ILoggable {

	protected static final String SUCCESS_STATUS = "Success";
	protected static final String FAILURE_STATUS = "Failure";

	protected final MainMessageSender messageSender;

	protected AbstractMessageProcessor(MainMessageSender messageSender) {
		this.messageSender = messageSender;
	}

	protected abstract boolean process(String type, long id, JsonNode content) throws InterruptedException;
	protected abstract boolean verifyMessageContent(JsonNode content);

	protected boolean verifyReplyContent(JsonNode content) {
		return content.isStringValue("status") &&
				content.isNode("message");
	}

	protected boolean success(String type, long id) {
		return messageSender.sendMessage(type, id, generateReply(SUCCESS_STATUS, ""));
	}

	protected boolean failure(String type, long id, String message) {
		getLogger().warning(message);
		messageSender.sendMessage(type, id, generateReply(FAILURE_STATUS, message));
		return false;
	}

	protected JsonNode generateReply(String status, String message) {
		return JsonNodeFactories.object(
				JsonNodeFactories.field("status", JsonNodeFactories.string(status)),
				JsonNodeFactories.field("message", JsonNodeFactories.string(message))
				);
	}

	protected JsonNode generateReply(String status, JsonNode message) {
		return JsonNodeFactories.object(
				JsonNodeFactories.field("status", JsonNodeFactories.string(status)),
				JsonNodeFactories.field("message", message)
				);
	}
}
