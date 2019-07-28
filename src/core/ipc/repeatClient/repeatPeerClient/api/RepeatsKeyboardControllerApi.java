package core.ipc.repeatClient.repeatPeerClient.api;

import core.ipc.repeatClient.repeatPeerClient.RepeatPeerServiceClientWriter;
import core.ipc.repeatServer.processors.IpcMessageType;

public class RepeatsKeyboardControllerApi extends AbstractRepeatsClientApi {

	protected RepeatsKeyboardControllerApi(RepeatPeerServiceClientWriter repeatPeerServiceClientWriter) {
		super(repeatPeerServiceClientWriter);
	}

	public void press(int key) {
		DeviceCommand command = commandBuilder().action("press").parameters(key).build();
		waitAndGetResponseIfSuccess(IpcMessageType.ACTION, command);
	}

	public void release(int key) {
		DeviceCommand command = commandBuilder().action("release").parameters(key).build();
		waitAndGetResponseIfSuccess(IpcMessageType.ACTION, command);
	}

	public void type(int... keys) {
		DeviceCommand command = commandBuilder().action("type").parameters(keys).build();
		waitAndGetResponseIfSuccess(IpcMessageType.ACTION, command);
	}

	public void type(String... strings) {
		DeviceCommand command = commandBuilder().action("type_string").parameters(strings).build();
		waitAndGetResponseIfSuccess(IpcMessageType.ACTION, command);
	}

	public void combination(int... keys) {
		DeviceCommand command = commandBuilder().action("combination").parameters(keys).build();
		waitAndGetResponseIfSuccess(IpcMessageType.ACTION, command);
	}

	private DeviceCommandBuilder commandBuilder() {
		return DeviceCommandBuilder.of().device("keyboard");
	}
}
