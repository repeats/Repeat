package core.ipc.repeatClient;

import java.io.File;

import com.sun.istack.internal.logging.Logger;

public abstract class IPCClientService extends IIPCService {
	private static final Logger LOGGER = Logger.getLogger(IPCClientService.class);

	protected File executingProgram; //The program used to execute this ipc client

	public void setExecutingProgram(File executablePath) {
		if (!executablePath.canExecute()) {
			LOGGER.warning("File is not executable");
		}

		this.executingProgram = executablePath;
	}
}
