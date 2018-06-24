package core.ipc.repeatClient;

import java.util.logging.Logger;

import core.ipc.IPCServiceManager;
import core.ipc.IPCServiceName;
import core.languageHandler.Language;
import staticResources.BootStrapResources;

public class PythonIPCClientService extends IPCClientService {

	@Override
	public String getName() {
		return "Python IPC client";
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(PythonIPCClientService.class.getName());
	}

	@Override
	protected String[] getLaunchCmd() {
		String pythonBinary = executingProgram.getAbsolutePath();
		String mainFile = BootStrapResources.getBootstrapResource(Language.PYTHON).getIPCClient().getAbsolutePath();
		int port = IPCServiceManager.getIPCService(IPCServiceName.CONTROLLER_SERVER).getPort();

		return new String[] { pythonBinary, "-u", mainFile, "--port", port + ""};
	}
}
