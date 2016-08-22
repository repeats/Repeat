package core.ipc.repeatServer.processors;

import java.util.List;

import argo.jdom.JsonNode;
import core.ipc.repeatServer.MainMessageSender;

/**
 * This class represents the message processor for any system action.
 *
 * A received message from the lower layer (central processor) will have the following JSON contents:
 * {
 * 		"device": reserved field. Must be empty string,
 * 		"action": action depending on the type parsed by lower layer,
 * 		"parameters": parameters for this action
 * }
 *
 * The possible actions for system_host are:
 * 1) Keep alive : keep this connection alive. If this is not received frequently, the system will terminate connection to client
 *
 * The possible actions for system client are:
 * 1) identify(name) : identify the client system as the remote compiler for a certain language.
 *
 * @author HP Truong
 *
 */
public class SystemRequestProcessor extends AbstractMessageProcessor {

	private final ServerMainProcessor holder;

	protected SystemRequestProcessor(MainMessageSender messageSender, ServerMainProcessor holder) {
		super(messageSender);
		this.holder = holder;
	}

	@Override
	public boolean process(String type, long id, JsonNode content) {
		if (!verifyMessageContent(content)) {
			getLogger().warning("Error in verifying message content " + content);
			return false;
		}

		String device = content.getStringValue("device");
		String action = content.getStringValue("action");
		List<JsonNode> paramNodes = content.getArrayNode("parameters"); // Unused

		if (IpcMessageType.identify(type) == IpcMessageType.SYSTEM_HOST) {
			if (action.equals("keep_alive")) {
				return success(type, id);
			}
		} else if (IpcMessageType.identify(type) == IpcMessageType.SYSTEM_CLIENT) {
			if (action.equals("identify")) {
				if (paramNodes.size() != 2) {
					getLogger().warning("Unexpected identity to have " + paramNodes.size() + " parameters.");
					return false;
				}

				String name;
				JsonNode nameNode = paramNodes.get(0);
				if (!nameNode.isStringValue()) {
					getLogger().warning("Identity must be a string.");
					return false;
				}

				int port;
				JsonNode portNode = paramNodes.get(1);
				if (!portNode.isStringValue()) {
					getLogger().warning("Port number must be a parsable string.");
					return false;
				} else {
					try {
						port = Integer.parseInt(portNode.getStringValue());
					} catch (NumberFormatException e) {
						getLogger().warning("Port number must be a number.");
						return false;
					}
				}

				name = nameNode.getStringValue();
				TaskProcessorManager.identifyProcessor(name, port, holder.getTaskProcessor());
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
				content.isArrayNode("parameters");
	}
}
