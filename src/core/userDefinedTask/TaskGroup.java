package core.userDefinedTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.keyChain.managers.GlobalEventsManager;
import core.languageHandler.compiler.DynamicCompilerManager;
import utilities.IJsonable;

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

	/**
	 * Get task based on given index, returning null if index out of bound.
	 */
	public UserDefinedAction getTask(int index) {
		if (index < 0 || index >= tasks.size()) {
			return null;
		}

		return tasks.get(index);
	}

	/**
	 * Get first task with given name, or null if no such task exists.
	 */
	public UserDefinedAction getTask(String name) {
		for (UserDefinedAction task : tasks) {
			if (task.getName().equals(name)) {
				return task;
			}
		}
		return null;
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

	public void setEnabled(boolean enabled, GlobalEventsManager keyManager) {
		if (keyManager == null) {
			return;
		} else {
			if (enabled) {
				for (UserDefinedAction task : tasks) {
					if (task.isEnabled()) {
						Set<UserDefinedAction> collisions = keyManager.isTaskRegistered(task);
						if (collisions.isEmpty()) {
							keyManager.registerTask(task);
						} else { // Revert everything and exit
							unregisterAll(keyManager);
							GlobalEventsManager.showCollisionWarning(null, collisions);
							return;
						}
					}
				}
			} else {
				unregisterAll(keyManager);
			}
			setEnabled(enabled);
		}
	}

	private void unregisterAll(GlobalEventsManager keyManager) {
		for (UserDefinedAction task : tasks) {
			keyManager.unregisterTask(task);
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

	public static TaskGroup parseJSON(DynamicCompilerManager factory, JsonNode node) {
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
