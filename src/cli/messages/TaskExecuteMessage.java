package cli.messages;

import java.util.HashMap;
import java.util.Map;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import argo.jdom.JsonStringNode;
import utilities.IJsonable;

public class TaskExecuteMessage implements IJsonable {
	private TaskIdentifier taskIdentifier;

	public TaskExecuteMessage() {}

	private TaskExecuteMessage(TaskIdentifier taskIdentifier) {
		this.taskIdentifier = taskIdentifier;
	}

	@Override
	public JsonRootNode jsonize() {
		Map<JsonStringNode, JsonNode> data = new HashMap<>();
		if (taskIdentifier != null) {
			data.put(JsonNodeFactories.string("task_identifier"), taskIdentifier.jsonize());
		}
		return JsonNodeFactories.object(data);
	}

	public static TaskExecuteMessage parseJSON(JsonNode node) {
		TaskIdentifier taskIdentifier = null;
		if (node.isObjectNode("task_identifier")) {
			taskIdentifier = TaskIdentifier.parseJSON(node.getNode("task_identifier"));
		}

		return new TaskExecuteMessage(taskIdentifier);
	}

	public TaskIdentifier getTaskIdentifier() {
		return taskIdentifier;
	}

	public TaskExecuteMessage setTaskIdentifier(TaskIdentifier taskIdentifier) {
		this.taskIdentifier = taskIdentifier;
		return this;
	}
}
