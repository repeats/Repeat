package core.ipc;

import java.io.IOException;

import core.ipc.repeatClient.PythonIPCClientService;
import core.ipc.repeatServer.ControllerServer;

public final class IPCServiceManager {

//	private static final Logger LOGGER = Logger.getLogger(IPCServiceManager.class.getName());

	public static final int IPC_SERVICE_COUNT = 2;
	private static final long INTER_SERVICE_BOOT_TIME_MS = 2000;
	private static final IIPCService[] ipcServices;

	static {
		ipcServices = new IIPCService[IPC_SERVICE_COUNT];
		ipcServices[IPCServiceName.CONTROLLER_SERVER.value()] =  new ControllerServer();
		ipcServices[IPCServiceName.PYTHON.value()] = new PythonIPCClientService();
	}

	public static IIPCService getIPCService(IPCServiceName name) {
		return ipcServices[name.value()];
	}

	public static IIPCService getIPCService(int index) {
		if (index >= IPC_SERVICE_COUNT) {
			return null;
		}

		return ipcServices[index];
	}

	public static void initiateServices() throws IOException {
		((ControllerServer)IPCServiceManager.getIPCService(IPCServiceName.CONTROLLER_SERVER)).startRunning();

		try {
			Thread.sleep(INTER_SERVICE_BOOT_TIME_MS);
		} catch (InterruptedException e) {
		}

		((PythonIPCClientService)IPCServiceManager.getIPCService(IPCServiceName.PYTHON)).startRunning();
	}

	public static void stopServices() throws IOException {
		PythonIPCClientService python = ((PythonIPCClientService)IPCServiceManager.getIPCService(IPCServiceName.PYTHON));

		do {
			python.stopRunning();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (python.isRunning());

		ControllerServer controller = ((ControllerServer)IPCServiceManager.getIPCService(IPCServiceName.CONTROLLER_SERVER));

		do {
			controller.stopRunning();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (controller.isRunning());
	}

	private IPCServiceManager() {}
}
