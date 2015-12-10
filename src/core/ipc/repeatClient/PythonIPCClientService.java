package core.ipc.repeatClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import staticResources.PythonResources;

public class PythonIPCClientService extends IPCClientService {

	private static final long TIMEOUT_MS = 5000;

	private Thread mainThread, forceDestroyThread;
	private Process mainProcess;
	private BufferedReader input;

	@Override
	public void start() throws IOException {
		final String[] cmd = { executingProgram.getAbsolutePath(), "-u", PythonResources.PYTHON_IPC_CLIENT.getAbsolutePath() };

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
						getLogger().log(Level.WARNING, "Failed to close input stream for python ipc client", e);
					}
			    }
			}
		};

		mainThread.start();
	}

	@Override
	public void stop() throws IOException {
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
	public boolean isRunning() {
		boolean result = mainThread != null && mainThread.isAlive();
		if (!result) {
			forceDestroyThread = null;
		}
		return result;
	}

	@Override
	public String getName() {
		return "Python IPC client";
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(PythonIPCClientService.class.getName());
	}
}
