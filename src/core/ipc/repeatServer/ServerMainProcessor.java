package core.ipc.repeatServer;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import utilities.JSONUtility;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;
import core.ILoggable;
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
	 * Each request has the form
	 * {
	 * 	"type" : type,
	 *  "id" : id,
	 *  "content" : content
	 * }
	 * where type is among the types listed as constants above
	 *
	 *
	 * {
	 * 		"device": deviceName,
	 * 		"action": action,
	 * 		"params": [param1, param2, ...]
	 * }
	 * where param is the list of input parameters the action can take
	 *
	 * The following table describes the possible actions
	 * Note that \<type\>... denotes that the action can take infinitely many parameters of the specified type
	 *  ________________________________________________________________________________________________________________________
	 *	| Device   | Action      | Param1    | Param2 | Param3    | Description                                                 |
	 *	|----------|-------------|-----------|--------|-----------|-------------------------------------------------------------|
	 *	| mouse    | leftClick   | None      | None   | None      |	Left click at the current cursor position                   |
	 *	| mouse    | leftClick   | int       | int    | None      | Left click at the position (param1, param2)                 |
	 *	| mouse    | rightClick  | None      | None   | None      | Right click at the current cursor position                  |
	 *	| mouse    | rightClick  | int       | int    | None      | Right click at the position (param1, param2)                |
	 *	| mouse    | move        | int       | int    | None      | Move mouse cursor to position (param1, param2)              |
	 *	| mouse    | moveBy      | int       | int    | None      | Move mouse cursor by (param1, param2) from current position |
	 *	| keyboard | type        | int...    | None   | None      | Type keys sequentially (key code is from KeyEvent class)    |
	 *	| keyboard | typeString  | string... | None   | None      | Type strings sequentially (cannot type special characters)  |
	 *	| keyboard | combination | int...    | None   | None      | Perform a key combination                                   |
	 *  | system   | keepAlive   | None      | None   | None      | Keep connection alive                                       |
	 *  |__________|_____________|___________|________|___________|_____________________________________________________________|
	 */

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
