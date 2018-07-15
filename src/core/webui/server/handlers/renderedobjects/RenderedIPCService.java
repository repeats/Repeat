package core.webui.server.handlers.renderedobjects;

import core.ipc.IIPCService;

public class RenderedIPCService {
	private String name;
	private String port;
	private String running;
	private String launchAtStartup;

	public static RenderedIPCService fromIPCService(IIPCService service) {
		RenderedIPCService output = new RenderedIPCService();
		output.name = service.getName();
		output.port = service.getPort() + "";
		output.running = service.isRunning() + "";
		output.launchAtStartup = service.isLaunchAtStartup() + "";
		return output;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRunning() {
		return running;
	}

	public void setRunning(String running) {
		this.running = running;
	}

	public String getLaunchAtStartup() {
		return launchAtStartup;
	}

	public void setLaunchAtStartup(String launchAtStartup) {
		this.launchAtStartup = launchAtStartup;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
}
