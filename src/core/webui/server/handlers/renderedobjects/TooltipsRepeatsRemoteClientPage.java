package core.webui.server.handlers.renderedobjects;

public class TooltipsRepeatsRemoteClientPage {
	private String add = "Add a new remote client.";
	private String delete = "Stop and remove the selected remote client.";
	private String run = "Start the selected remote client.";
	private String stop = "Stop the selected remote client.";

	public String getAdd() {
		return add;
	}
	public void setAdd(String add) {
		this.add = add;
	}
	public String getDelete() {
		return delete;
	}
	public void setDelete(String delete) {
		this.delete = delete;
	}
	public String getRun() {
		return run;
	}
	public void setRun(String run) {
		this.run = run;
	}
	public String getStop() {
		return stop;
	}
	public void setStop(String stop) {
		this.stop = stop;
	}
}
