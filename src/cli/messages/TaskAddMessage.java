package cli.messages;

import java.util.HashMap;
import java.util.Map;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import argo.jdom.JsonStringNode;
import utilities.IJsonable;

public class TaskAddMessage implements IJsonable {

	private TaskIdentifier taskIdentifier;
	private String filePath;

	private TaskAddMessage() {}
	public static TaskAddMessage of() {
		return new TaskAddMessage();
	}

	private TaskAddMessage(TaskIdentifier taskIdentifier, String name, String filePath) {
		this.taskIdentifier = taskIdentifier;
		this.filePath = filePath;
	}

	public static TaskAddMessage parseJSON(JsonNode node) {
		TaskIdentifier taskIdentifier = null;
		if (node.isObjectNode("task_identifier")) {
			taskIdentifier = TaskIdentifier.parseJSON(node.getNode("task_identifier"));
		}

		String name = "";
		if (node.isStringValue("name")) {
			name = node.getStringValue("name");
		}

		String filePath = "";
		if (node.isStringValue("file_path")) {
			filePath = node.getStringValue("file_path");
		}

		return new TaskAddMessage(taskIdentifier, name, filePath);
	}

	@Override
	public JsonRootNode jsonize() {
		Map<JsonStringNode, JsonNode> data = new HashMap<>();
		if (taskIdentifier != null) {
			data.put(JsonNodeFactories.string("task_identifier"), taskIdentifier.jsonize());
		}
		data.put(JsonNodeFactories.string("file_path"), JsonNodeFactories.string(filePath));

		return JsonNodeFactories.object(data);
	}

	public TaskIdentifier getTaskIdentifier() {
		return taskIdentifier;
	}

	public TaskAddMessage setTaskIdentifier(TaskIdentifier taskIdentifier) {
		this.taskIdentifier = taskIdentifier;
		return this;
	}

	public String getFilePath() {
		return filePath;
	}

	public TaskAddMessage setFilePath(String filePath) {
		this.filePath = filePath;
		return this;
	}
}
