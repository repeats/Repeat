package core.ipc.repeatServer;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import utilities.ILoggable;
import utilities.JSONUtility;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;
import core.controller.Core;

public class ServerMainProcessor implements ILoggable {

	protected static final String TYPE_ACTION = "action";
	protected static final String TYPE_TASK = "task";
	protected static final String TYPE_SYSTEM_HOST = "system_host";
	protected static final String TYPE_SYSTEM_CLIENT = "system_client";

	private final Map<String, AbstractMessageProcessor> messageProcesssors;
	private final ControllerRequestProcessor actionProcessor;
	private final TaskProcessor taskProcessor;
	private final SystemRequestProcessor systemProcessor;

	protected ServerMainProcessor(Core core, MainMessageSender messageSender) {
		messageProcesssors = new HashMap<>();

		actionProcessor = new ControllerRequestProcessor(messageSender, core);
		taskProcessor = new TaskProcessor(messageSender);
		systemProcessor = new SystemRequestProcessor(messageSender, this);

		messageProcesssors.put(TYPE_ACTION, actionProcessor);
		messageProcesssors.put(TYPE_TASK, taskProcessor);
		messageProcesssors.put(TYPE_SYSTEM_HOST, systemProcessor);
		messageProcesssors.put(TYPE_SYSTEM_CLIENT, systemProcessor);
	}

	/**
	 * Parse a request from client.
	 * @param message request from client as JSON string
	 * @param core Core controller that will execute the action
	 * @return list of actions need to perform in order
	 */
	protected boolean processRequest(String message) {
		JsonRootNode root = JSONUtility.jsonFromString(message);
		if (root == null || !verifyMessage(root)) {
			getLogger().warning("Invalid messaged received " + message);
			return false;
		}

		System.out.println("Receive " + message);
		String type = root.getStringValue("type");
		long id = Long.parseLong(root.getNumberValue("id"));
		JsonNode content = root.getNode("content");

		try {
			messageProcesssors.get(type).process(type, id, content);
			return true;
		} catch (InterruptedException e) {
			getLogger().log(Level.WARNING, "Interrupted while processing message", e);
			return false;
		}
	}

	private boolean verifyMessage(JsonRootNode message) {
		return message.isStringValue("type") &&
				message.isNumberValue("id") &&
				message.isObjectNode("content") &&
				messageProcesssors.containsKey(message.getStringValue("type"));
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(ServerMainProcessor.class.getName());
	}

	public TaskProcessor getTaskProcessor() {
		return taskProcessor;
	}
}
