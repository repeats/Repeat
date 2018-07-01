package cli.messages;

import java.util.logging.Logger;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import utilities.IJsonable;

public class TaskGroupMessage implements IJsonable {

	private static final Logger LOGGER = Logger.getLogger(TaskGroupMessage.class.getName());

	private static final int UNKNOWN_INDEX = -1;

	private String name;
	private int index;

	private TaskGroupMessage() {}
	public static TaskGroupMessage of() {
		return new TaskGroupMessage();
	}

	private TaskGroupMessage(String name, int index) {
		this.name = name;
		this.index = index;
	}

	public static TaskGroupMessage parseJSON(JsonNode node) {
		if (node.isNumberValue("index")) {
			int index = Integer.parseInt(node.getNumberValue("index"));
			return new TaskGroupMessage("", index);
		}

		if (node.isStringValue("name")) {
			return new TaskGroupMessage(node.getStringValue("name"), UNKNOWN_INDEX);
		}

		LOGGER.warning("Unable to parse task group message. Neither field name nor index was present.");
		return null;
	}

	@Override
	public JsonRootNode jsonize() {
		return JsonNodeFactories.object(JsonNodeFactories.field("index", JsonNodeFactories.number(index)),
				JsonNodeFactories.field("name", JsonNodeFactories.string(name == null ? "" : name)));
	}

	public String getName() {
		return name;
	}

	public TaskGroupMessage setName(String name) {
		this.name = name;
		return this;
	}

	public boolean hasIndex() {
		return index != UNKNOWN_INDEX;
	}

	public int getIndex() {
		return index;
	}

	public TaskGroupMessage setIndex(int index) {
		this.index = index;
		return this;
	}
}
