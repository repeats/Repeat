package core.ipc.repeatClient.repeatPeerClient.api;

import java.util.List;

import utilities.json.AutoJsonable;

@SuppressWarnings("unused")
public abstract class DeviceCommand extends AutoJsonable {

	public static class IntDeviceCommand extends DeviceCommand {
		private String device;
		private String action;
		private List<Integer> parameters;

		IntDeviceCommand(String device, String action, List<Integer> parameters) {
			this.device = device;
			this.action = action;
			this.parameters = parameters;
		}
	}

	public static class StringDeviceCommand extends DeviceCommand {
		private String device;
		private String action;
		private List<String> parameters;

		StringDeviceCommand(String device, String action, List<String> parameters) {
			this.device = device;
			this.action = action;
			this.parameters = parameters;
		}
	}
}
