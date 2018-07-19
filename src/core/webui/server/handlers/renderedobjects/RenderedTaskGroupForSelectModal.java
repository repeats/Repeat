package core.webui.server.handlers.renderedobjects;

import core.userDefinedTask.TaskGroup;

public class RenderedTaskGroupForSelectModal {
	private String name;
	private String selected;

	public static RenderedTaskGroupForSelectModal fromTaskGroups(TaskGroup group, TaskGroup currentGroup) {
		RenderedTaskGroupForSelectModal output = new RenderedTaskGroupForSelectModal();
		output.name = group.getName();
		output.selected = group == currentGroup ? "selected" : "";
		return output;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}
}
