package core.ipc.repeatServer;

import java.util.List;
import java.util.logging.Logger;

import argo.jdom.JsonNode;

public class SystemRequestProcessor extends AbstractMessageProcessor {

	private final ServerMainProcessor holder;

	protected SystemRequestProcessor(MainMessageSender messageSender, ServerMainProcessor holder) {
		super(messageSender);
		this.holder = holder;
	}

	@Override
	protected boolean process(String type, long id, JsonNode content) {
		if (!verifyMessageContent(content)) {
			getLogger().warning("Error in verifying message content " + content);
			return false;
		}

		String device = content.getStringValue("device");
		String action = content.getStringValue("action");
		List<JsonNode> paramNodes = content.getArrayNode("params"); //Unused

		if (type.equals(ServerMainProcessor.TYPE_SYSTEM_HOST)) {
			if (action.equals("keep_alive")) {
				return success(type, id);
			}
		} else if (type.equals(ServerMainProcessor.TYPE_SYSTEM_CLIENT)) {
			if (action.equals("identify")) {
				if (paramNodes.size() != 1) {
					getLogger().warning("Unexpected identity to have 2 params.");
					return false;
				}
				JsonNode nameNode = paramNodes.get(0);
				if (!nameNode.isStringValue()) {
					getLogger().warning("Identity must be a string.");
					return false;
				}

				String name = nameNode.getStringValue();
				TaskProcessorManager.identifyProcessor(name, holder.getTaskProcessor());
				return success(type, id);
			}
		}

		getLogger().warning("Unsupported operation [" + device + ", " + action + "]");
		return false;
	}

	@Override
	protected boolean verifyMessageContent(JsonNode content) {
		return content.isStringValue("device") &&
				content.getStringValue("device").startsWith("system") &&
				content.isStringValue("action") &&
				content.isArrayNode("params");
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(SystemRequestProcessor.class.getName());
	}
}
