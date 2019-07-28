package core.ipc.repeatClient.repeatPeerClient.api;

import core.ipc.repeatClient.repeatPeerClient.RepeatPeerServiceClientWriter;

public class RepeatsClientApi {

	protected RepeatsMouseControllerApi mouse;
	protected RepeatsKeyboardControllerApi keyboard;
	protected RepeatsToolApi tool;

	public RepeatsClientApi(RepeatPeerServiceClientWriter repeatPeerServiceClientWriter) {
		mouse = new RepeatsMouseControllerApi(repeatPeerServiceClientWriter);
		keyboard = new RepeatsKeyboardControllerApi(repeatPeerServiceClientWriter);
		tool = new RepeatsToolApi(repeatPeerServiceClientWriter);
	}

	public RepeatsMouseControllerApi mouse() {
		return mouse;
	}

	public RepeatsKeyboardControllerApi keyboard() {
		return keyboard;
	}

	public RepeatsToolApi tool() {
		return tool;
	}
}
