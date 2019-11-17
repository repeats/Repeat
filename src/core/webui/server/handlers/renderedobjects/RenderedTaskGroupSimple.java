package core.webui.server.handlers.renderedobjects;

import core.userDefinedTask.TaskGroup;

public class RenderedTaskGroupSimple {
	private String id;
	private String name;

	protected static RenderedTaskGroupSimple fromTaskGroup(TaskGroup group) {
		RenderedTaskGroupSimple output =  new RenderedTaskGroupSimple();
		output.id = group.getGroupId();
		output.name = group.getName();
		return output;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}
}
