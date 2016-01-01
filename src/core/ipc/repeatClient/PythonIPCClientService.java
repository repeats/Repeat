package core.ipc.repeatClient;

import java.util.logging.Logger;

import staticResources.BootStrapResources;
import core.languageHandler.Language;

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
		return new String[] { executingProgram.getAbsolutePath(), "-u", BootStrapResources.getBootstrapResource(Language.PYTHON).getIPCClient().getAbsolutePath() };
	}
}
