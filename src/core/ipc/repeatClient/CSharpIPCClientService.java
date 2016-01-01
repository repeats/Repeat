package core.ipc.repeatClient;

import java.util.logging.Logger;

import staticResources.BootStrapResources;
import utilities.OSIdentifier;
import core.languageHandler.Language;

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
			return new String[] { executingProgram.getAbsolutePath() };
		} else {
			getLogger().info("C# compiler disabled. This only runs on Windows operating system.");
			return null;
		}
	}
}
