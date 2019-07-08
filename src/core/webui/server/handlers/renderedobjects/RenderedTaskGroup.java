package core.webui.server.handlers.renderedobjects;

import core.userDefinedTask.TaskGroup;

public class RenderedTaskGroup {
	private String id;
	private String name;
	private int taskCount;
	private String enabled;
	private String selected;

	public static RenderedTaskGroup fromTaskGroup(TaskGroup group, boolean selected) {
		RenderedTaskGroup output = new RenderedTaskGroup();
		output.id = group.getGroupId();
		output.name = group.getName();
		output.taskCount = group.getTasks().size();
		output.enabled = group.isEnabled() + "";
		output.selected = selected + "";
		return output;
	}


	public String getId() {
		return id;
	}
	public void setId(String groupId) {
		this.id = groupId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getTaskCount() {
		return taskCount;
	}
	public void setTaskCount(int taskCount) {
		this.taskCount = taskCount;
	}
	public String getEnabled() {
		return enabled;
	}
	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}
	public String getSelected() {
		return selected;
	}
	public void setSelected(String selected) {
		this.selected = selected;
	}
}
