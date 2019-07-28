package core.ipc.repeatClient.repeatPeerClient.api;

import core.ipc.repeatClient.repeatPeerClient.RepeatPeerServiceClientWriter;
import core.ipc.repeatServer.processors.IpcMessageType;

public class RepeatsToolApi extends AbstractRepeatsClientApi {

	protected RepeatsToolApi(RepeatPeerServiceClientWriter repeatPeerServiceClientWriter) {
		super(repeatPeerServiceClientWriter);
	}

	public String execute(String cmd) {
		DeviceCommand command = commandBuilder().action("execute").parameters(cmd).build();
		return waitAndGetResponseIfSuccess(IpcMessageType.ACTION, command);
	}

	public String execute(String cmd, String cwd) {
		DeviceCommand command = commandBuilder().action("execute").parameters(cmd, cwd).build();
		return waitAndGetResponseIfSuccess(IpcMessageType.ACTION, command);
	}

	public String getClipboard() {
		DeviceCommand command = commandBuilder().action("get_clipboard").build();
		return waitAndGetResponseIfSuccess(IpcMessageType.ACTION, command);
	}

	public void setClipboard(String data) {
		DeviceCommand command = commandBuilder().action("set_clipboard").parameters(data).build();
		waitAndGetResponseIfSuccess(IpcMessageType.ACTION, command);
	}

	private DeviceCommandBuilder commandBuilder() {
		return DeviceCommandBuilder.of().device("tool");
	}
}
