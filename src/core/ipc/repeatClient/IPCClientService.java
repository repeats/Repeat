package core.ipc.repeatClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;

import utilities.StringUtilities;

import com.sun.istack.internal.logging.Logger;

import core.ipc.IIPCService;

public abstract class IPCClientService extends IIPCService {

	private static final Logger LOGGER = Logger.getLogger(IPCClientService.class);
	protected static final long TIMEOUT_MS = 5000;

	protected File executingProgram; //The program used to execute this ipc client
	protected Thread mainThread, forceDestroyThread;
	protected Process mainProcess;
	protected BufferedReader input;

	public void setExecutingProgram(File executablePath) {
		if (!executablePath.canExecute()) {
			LOGGER.warning("File is not executable");
		}

		this.executingProgram = executablePath;
	}

	@Override
	public final void start() throws IOException {
		if (!executingProgram.exists()) {
			getLogger().warning("Launcher " + executingProgram.getAbsolutePath() + " does not exist.");
			return;
		}

		if (!executingProgram.canExecute()) {
			getLogger().warning("Launcher " + executingProgram.getAbsolutePath() + " is not executable.");
			return;
		}

		final String[] cmd = getLaunchCmd();
		if (cmd == null) {
			getLogger().warning("Unable to retrieve launch cmd for " + getName());
			return;
		} else {
			getLogger().info("Executing " + StringUtilities.join(cmd, " "));
		}

		mainThread = new Thread() {
			@Override
			public void run() {
				try {
					String line;
					ProcessBuilder processBuilder = new ProcessBuilder(cmd);
					processBuilder.redirectErrorStream(true);
					mainProcess = processBuilder.start();

					input = new BufferedReader(new InputStreamReader(mainProcess.getInputStream()));

					while ((line = input.readLine()) != null) {
						String trimmed = line.trim();
						if (trimmed.length() == 0) {
							continue;
						}

						getLogger().info(trimmed);
					}

					mainProcess.waitFor();
			    } catch (Exception e) {
			    	getLogger().log(Level.WARNING, "Encounter exception while running process: " + cmd[0] + cmd[1], e);
			    } finally {
			    	try {
						input.close();
					} catch (IOException e) {
						getLogger().log(Level.WARNING, "Failed to close input stream for " + getName(), e);
					}
			    }
			}
		};

		mainThread.start();
	}

	@Override
	public final void stop() throws IOException {
		if (forceDestroyThread != null) {
			getLogger().info("Waiting for " + getName() + " to terminate...");
			return;
		}

		forceDestroyThread = new Thread() {
			@Override
			public void run() {
				mainProcess.destroy();
				getLogger().info("Destroyed");

				try {
					Thread.sleep(TIMEOUT_MS);
				} catch (InterruptedException e) {
					getLogger().log(Level.WARNING, "Interrupted while waiting for " + getName() + " to terminate", e);
				}

				if (mainProcess.isAlive()) {
					getLogger().info("Forcing " + getName() + " termination");
					mainProcess.destroyForcibly();
				}
			}
		};
		forceDestroyThread.start();
	}

	@Override
	public final boolean isRunning() {
		boolean result = mainThread != null && mainThread.isAlive();
		if (!result) {
			forceDestroyThread = null;
		}
		return result;
	}

	protected abstract String[] getLaunchCmd();
}
