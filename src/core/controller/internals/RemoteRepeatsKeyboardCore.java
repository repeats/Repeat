package core.controller.internals;

import core.ipc.repeatClient.repeatPeerClient.api.RepeatsClientApi;

public class RemoteRepeatsKeyboardCore extends AbstractKeyboardCoreImplementation {

	private RepeatsClientApi api;

	public RemoteRepeatsKeyboardCore(RepeatsClientApi api) {
		this.api = api;
	}

	@Override
	public void type(String... strings) {
		api.keyboard().type(strings);
	}

	@Override
	public void type(char... chars) {
		api.keyboard().type(chars);
	}

	@Override
	public void type(int... keys) throws InterruptedException {
		api.keyboard().type(keys);
	}

	@Override
	public void combination(int... keys) {
		api.keyboard().combination(keys);
	}

	@Override
	public void hold(int key, int duration) throws InterruptedException {
	}

	@Override
	public void press(int... keys) {
		api.keyboard().press(keys);
	}

	@Override
	public void release(int... keys) {
		api.keyboard().release(keys);
	}

	@Override
	public boolean isLocked(int key) {
		return api.keyboard().isLocked(key);
	}

}
