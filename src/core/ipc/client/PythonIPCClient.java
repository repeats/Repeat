package core.ipc.client;

import com.sun.istack.internal.logging.Logger;

public class PythonIPCClient extends AbstractIPCClient {

	private static String SERVER_ADDRESS = "localhost";
	private static int SERVER_PORT = 9998;

	public PythonIPCClient() {
		super(SERVER_ADDRESS, SERVER_PORT);
	}

	@Override
	protected Logger getLogger() {
		return Logger.getLogger(PythonIPCClient.class);
	}

}
