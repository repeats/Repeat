package core.webui.server.handlers.renderedobjects;

public class TooltipsIPCPage {
	private String run = "Run the selected IPC service.";
	private String stop = "Stop the selected IPC service.";

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
