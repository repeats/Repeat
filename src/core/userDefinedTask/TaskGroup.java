package core.userDefinedTask;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.config.IJsonable;
import core.keyChain.GlobalKeysManager;
import core.languageHandler.compiler.DynamicCompilerFactory;

public class TaskGroup implements IJsonable {

	private String name;
	private boolean enabled;
	private final List<UserDefinedAction> tasks;

	public TaskGroup(String name, List<UserDefinedAction> tasks) {
		this.name = name;
		this.tasks = tasks;
		this.enabled = true;
	}

	public TaskGroup(String name) {
		this(name, new ArrayList<UserDefinedAction>());
	}

	public List<UserDefinedAction> getTasks() {
		return tasks;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isEnabled() {
		return enabled;
	}

	private void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setEnabled(boolean enabled, GlobalKeysManager keyManager) {
		setEnabled(enabled);

		if (keyManager == null) {
			return;
		} else {
			if (enabled) {
				for (UserDefinedAction task : tasks) {
					if (task.isEnabled()) {
						keyManager.registerKey(task.getHotkey(), task);
					}
				}
			} else {
				for (UserDefinedAction task : tasks) {
					keyManager.unregisterKey(task.getHotkey());
				}
			}
		}
	}

	@Override
	public JsonRootNode jsonize() {
		List<JsonNode> taskNodes = new ArrayList<>();
		for (UserDefinedAction task : tasks) {
			taskNodes.add(task.jsonize());
		}

		return JsonNodeFactories.object(
				JsonNodeFactories.field("name", JsonNodeFactories.string(name)),
				JsonNodeFactories.field("enabled", JsonNodeFactories.booleanNode(enabled)),
				JsonNodeFactories.field("tasks", JsonNodeFactories.array(taskNodes))
				);
	}

	public static TaskGroup parseJSON(DynamicCompilerFactory factory, JsonNode node) {
		try {
			TaskGroup output = new TaskGroup("");
			String name = node.getStringValue("name");
			output.name = name;

			for (JsonNode task : node.getArrayNode("tasks")) {
				UserDefinedAction action = UserDefinedAction.parseJSON(factory, task);
				if (action != null) {
					output.tasks.add(action);
				}
			}

			output.enabled = node.getBooleanValue("enabled");

			return output;
		} catch (Exception e) {
			Logger.getLogger(TaskGroup.class.getName()).log(Level.WARNING, "Exception parsing task group from JSON", e);
			return null;
		}
	}
}
