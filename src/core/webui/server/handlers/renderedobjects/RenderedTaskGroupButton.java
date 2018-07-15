package core.webui.server.handlers.renderedobjects;

import java.util.List;
import java.util.stream.Collectors;

import core.userDefinedTask.TaskGroup;

public class RenderedTaskGroupButton {
	private String current;
	private List<RenderedTaskGroupSimple> groups;

	public static RenderedTaskGroupButton fromTaskGroups(TaskGroup current, List<TaskGroup> groups) {
		RenderedTaskGroupButton output = new RenderedTaskGroupButton();
		output.current = current.getName();
		output.groups = groups.stream().map(RenderedTaskGroupSimple::fromTaskGroup).collect(Collectors.toList());
		return output;
	}

	public String getCurrent() {
		return current;
	}

	public void setCurrent(String current) {
		this.current = current;
	}

	public List<RenderedTaskGroupSimple> getGroups() {
		return groups;
	}

	public void setGroups(List<RenderedTaskGroupSimple> groups) {
		this.groups = groups;
	}
}
