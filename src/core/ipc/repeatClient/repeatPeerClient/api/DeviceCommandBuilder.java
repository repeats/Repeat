package core.ipc.repeatClient.repeatPeerClient.api;

import java.util.ArrayList;
import java.util.List;

import core.ipc.repeatClient.repeatPeerClient.api.DeviceCommand.IntDeviceCommand;
import core.ipc.repeatClient.repeatPeerClient.api.DeviceCommand.StringDeviceCommand;
import utilities.json.AutoJsonable;

public class DeviceCommandBuilder extends AutoJsonable {
	private String device;
	private String action;
	private List<String> stringParameters;
	private List<Integer> intParameters;

	public static DeviceCommandBuilder of() {
		return new DeviceCommandBuilder();
	}

	public DeviceCommandBuilder device(String device) {
		this.device = device;
		return this;
	}

	public DeviceCommandBuilder action(String action) {
		this.action = action;
		return this;
	}

	public DeviceCommandBuilder parameters() {
		this.stringParameters = new ArrayList<>();
		this.intParameters = null;
		return this;
	}

	public DeviceCommandBuilder parameters(String... parameters) {
		this.stringParameters = new ArrayList<>();
		for (String p : parameters) {
			this.stringParameters.add(p);
		}
		this.intParameters = null;
		return this;
	}

	public DeviceCommandBuilder parameters(int... parameters) {
		this.intParameters = new ArrayList<>();
		for (int p : parameters) {
			this.intParameters.add(p);
		}
		this.stringParameters = null;
		return this;
	}

	public DeviceCommand build() {
		if (stringParameters == null && intParameters == null) {
			stringParameters = new ArrayList<>();
		}

		if (stringParameters != null) {
			return new StringDeviceCommand(device, action, stringParameters);
		}
		return new IntDeviceCommand(device, action, intParameters);
	}
}
