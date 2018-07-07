package core.cli.messages;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import utilities.IJsonable;

public class TaskMessage implements IJsonable {

	private static final int UNKNOWN_INDEX = -1;

	private String name;
	private int index = UNKNOWN_INDEX;

	private TaskMessage() {}
	public static TaskMessage of() {
		return new TaskMessage();
	}

	private TaskMessage(String name, int index) {
		this.name = name;
		this.index = index;
	}

	public static TaskMessage parseJSON(JsonNode node) {
		int index = UNKNOWN_INDEX;
		String name = "";

		if (node.isNumberValue("index")) {
			index = Integer.parseInt(node.getNumberValue("index"));
		}

		if (node.isStringValue("name")) {
			name = node.getStringValue("name");
		}

		return new TaskMessage(name, index);
	}

	@Override
	public JsonRootNode jsonize() {
		return JsonNodeFactories.object(JsonNodeFactories.field("index", JsonNodeFactories.number(index)),
				JsonNodeFactories.field("name", JsonNodeFactories.string(name)));
	}

	public String getName() {
		return name;
	}

	public TaskMessage setName(String name) {
		this.name = name;
		return this;
	}

	public boolean hasIndex() {
		return index != UNKNOWN_INDEX;
	}

	public int getIndex() {
		return index;
	}

	public TaskMessage setIndex(int index) {
		this.index = index;
		return this;
	}
}
