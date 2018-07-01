package cli.messages;

import java.util.logging.Logger;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import utilities.IJsonable;

public class TaskMessage implements IJsonable {

	private static final Logger LOGGER = Logger.getLogger(TaskMessage.class.getName());

	private static final int UNKNOWN_INDEX = -1;

	private String name;
	private int index;

	private TaskMessage(String name, int index) {
		this.name = name;
		this.index = index;
	}

	public static TaskMessage parseJSON(JsonNode node) {
		if (node.isNumberValue("index")) {
			int index = Integer.parseInt(node.getNumberValue("index"));
			return new TaskMessage("", index);
		}

		if (node.isStringValue("name")) {
			return new TaskMessage(node.getStringValue("name"), UNKNOWN_INDEX);
		}

		LOGGER.warning("Unable to parse task message. Neither field name nor index was present.");
		return null;
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
