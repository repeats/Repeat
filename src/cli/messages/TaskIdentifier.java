package cli.messages;

import java.util.HashMap;
import java.util.Map;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import argo.jdom.JsonStringNode;
import utilities.IJsonable;

public class TaskIdentifier implements IJsonable {
	private TaskMessage task;
	private TaskGroupMessage group;

	private TaskIdentifier() {}
	public static TaskIdentifier of() {
		return new TaskIdentifier();
	}

	private TaskIdentifier(TaskMessage task, TaskGroupMessage group) {
		this.task = task;
		this.group = group;
	}

	public static TaskIdentifier parseJSON(JsonNode node) {
		TaskMessage task = null;
		if (node.isObjectNode("task")) {
			task = TaskMessage.parseJSON(node.getNode("task"));
		}

		TaskGroupMessage group = null;
		if (node.isObjectNode("group")) {
			group = TaskGroupMessage.parseJSON(node.getNode("group"));
		}

		return new TaskIdentifier(task, group);
	}

	@Override
	public JsonRootNode jsonize() {
		Map<JsonStringNode, JsonNode> data = new HashMap<>();
		if (task != null) {
			data.put(JsonNodeFactories.string("task"), task.jsonize());
		}
		if (group != null) {
			data.put(JsonNodeFactories.string("group"), group.jsonize());
		}
		return JsonNodeFactories.object(data);
	}

	public TaskMessage getTask() {
		return task;
	}

	public TaskIdentifier setTask(TaskMessage task) {
		this.task = task;
		return this;
	}

	public TaskGroupMessage getGroup() {
		return group;
	}

	public TaskIdentifier setGroup(TaskGroupMessage group) {
		this.group = group;
		return this;
	}
}
