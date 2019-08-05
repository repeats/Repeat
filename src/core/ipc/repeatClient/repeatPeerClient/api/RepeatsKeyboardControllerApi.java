package core.ipc.repeatClient.repeatPeerClient.api;

import argo.jdom.JsonNode;
import core.ipc.repeatClient.repeatPeerClient.RepeatPeerServiceClientWriter;
import core.ipc.repeatServer.processors.IpcMessageType;

public class RepeatsKeyboardControllerApi extends AbstractRepeatsClientApi {

	protected RepeatsKeyboardControllerApi(RepeatPeerServiceClientWriter repeatPeerServiceClientWriter) {
		super(repeatPeerServiceClientWriter);
	}

	public void press(int... keys) {
		DeviceCommand command = commandBuilder().action("press").parameters(keys).build();
		waitAndGetResponseIfSuccess(IpcMessageType.ACTION, command);
	}

	public void release(int... keys) {
		DeviceCommand command = commandBuilder().action("release").parameters(keys).build();
		waitAndGetResponseIfSuccess(IpcMessageType.ACTION, command);
	}

	public void type(int... keys) {
		DeviceCommand command = commandBuilder().action("type").parameters(keys).build();
		waitAndGetResponseIfSuccess(IpcMessageType.ACTION, command);
	}

	public void type(char... chars) {
		int[] params = new int[chars.length];
		for (int i = 0; i < params.length; i++) {
			params[i] = chars[i];
		}

		DeviceCommand command = commandBuilder().action("type_characters").parameters(params).build();
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

	public boolean isLocked(int key) {
		DeviceCommand command = commandBuilder().action("is_locked").parameters(key).build();
		JsonNode response = waitAndGetJsonResponseIfSuccess(IpcMessageType.ACTION, command);
		return response.getBooleanValue();
	}

	private DeviceCommandBuilder commandBuilder() {
		return DeviceCommandBuilder.of().device("keyboard");
	}
}
