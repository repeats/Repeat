package core.ipc.repeatClient;

import java.util.logging.Logger;

import core.ipc.IPCServiceManager;
import core.ipc.IPCServiceName;
import core.languageHandler.Language;
import staticResources.BootStrapResources;
import utilities.OSIdentifier;

public class CSharpIPCClientService extends IPCClientService {

	public CSharpIPCClientService() {
		this.executingProgram = BootStrapResources.getBootstrapResource(Language.CSHARP).getIPCClient();
	}

	@Override
	public String getName() {
		return "C# IPC client";
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(CSharpIPCClientService.class.getName());
	}

	@Override
	protected String[] getLaunchCmd() {
		if (OSIdentifier.IS_WINDOWS) {
			int port = IPCServiceManager.getIPCService(IPCServiceName.CONTROLLER_SERVER).getPort();
			return new String[] { executingProgram.getAbsolutePath(), "--port", port + "" };
		} else {
			getLogger().info("C# compiler disabled. This only runs on Windows operating system.");
			return null;
		}
	}
}
