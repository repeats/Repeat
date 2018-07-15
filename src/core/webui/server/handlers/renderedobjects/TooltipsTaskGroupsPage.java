package core.webui.server.handlers.renderedobjects;

public class TooltipsTaskGroupsPage {
	private String add = "Add a new task group.";
	private String remove = "Remove the selected task group.";
	private String up = "Move the selected task group up.";
	private String down = "Move the selected task group down.";

	public String getAdd() {
		return add;
	}
	public void setAdd(String add) {
		this.add = add;
	}
	public String getRemove() {
		return remove;
	}
	public void setRemove(String remove) {
		this.remove = remove;
	}
	public String getUp() {
		return up;
	}
	public void setUp(String up) {
		this.up = up;
	}
	public String getDown() {
		return down;
	}
	public void setDown(String down) {
		this.down = down;
	}
}
