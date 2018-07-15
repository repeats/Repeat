package core.webui.server.handlers.renderedobjects;

import core.userDefinedTask.TaskGroup;

public class RenderedTaskGroupSimple {
	private String name;

	protected static RenderedTaskGroupSimple fromTaskGroup(TaskGroup group) {
		RenderedTaskGroupSimple output =  new RenderedTaskGroupSimple();
		output.name = group.getName();
		return output;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
