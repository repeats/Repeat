package core.webui.server.handlers.renderedobjects;

public class TooltipsIndexPage {
	private String record = "Record mouse and keyboard activities.";
	private String replay = "Replay recorded activities.";
	private String compile = "Compile source code.";
	private String run = "Run compiled source code.";
	private String editCode = "Edit source code in default editor.";
	private String reload = "Reload editted source code after editted in default editor.";
	private String add = "Add the compiled action as a new task.";
	private String overwrite = "Overwrite selected task with the compiled action.";
	private String delete = "Delete selected task.";
	private String up = "Move selected task up.";
	private String down = "Move selected task down.";
	private String changeGroup = "Change the select task's group.";

	public String getRecord() {
		return record;
	}
	public void setRecord(String record) {
		this.record = record;
	}
	public String getReplay() {
		return replay;
	}
	public void setReplay(String replay) {
		this.replay = replay;
	}
	public String getCompile() {
		return compile;
	}
	public void setCompile(String compile) {
		this.compile = compile;
	}
	public String getRun() {
		return run;
	}
	public void setRun(String run) {
		this.run = run;
	}
	public String getEditCode() {
		return editCode;
	}
	public void setEditCode(String editCode) {
		this.editCode = editCode;
	}
	public String getReload() {
		return reload;
	}
	public void setReload(String reload) {
		this.reload = reload;
	}
	public String getAdd() {
		return add;
	}
	public void setAdd(String add) {
		this.add = add;
	}
	public String getOverwrite() {
		return overwrite;
	}
	public void setOverwrite(String overwrite) {
		this.overwrite = overwrite;
	}
	public String getDelete() {
		return delete;
	}
	public void setDelete(String delete) {
		this.delete = delete;
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
	public String getChangeGroup() {
		return changeGroup;
	}
	public void setChangeGroup(String changeGroup) {
		this.changeGroup = changeGroup;
	}
}
