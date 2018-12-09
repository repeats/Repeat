package nativehooks;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import utilities.StringUtilities;

public abstract class AbstractNativeHookEventProcessor {
	private static final Logger LOGGER = Logger.getLogger(AbstractNativeHookEventProcessor.class.getName());
	private static final long TIMEOUT_MS = 2000;

	private Process process;
	private Thread stdoutThread, stderrThread, forceDestroyThread;

	public abstract String getName();
	public abstract File getExecutionDir();
	public abstract String[] getCommand();
	public abstract void processStdout(String line);
	public abstract void processStderr(String line);

	public final void start() {
		if (process != null || stdoutThread != null || stderrThread != null) {
			LOGGER.warning("Hook is already running...");
			return;
		}
		File executableDir = getExecutionDir();
		if (!executableDir.isDirectory()) {
			LOGGER.warning(getName() + " executable directory " + getExecutionDir().getAbsolutePath() + " does not exist or is not a directory.");
			return;
		}

		String[] command = getCommand();
		LOGGER.info(getName() + ": running command $" + StringUtilities.join(command, " "));
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(command);
			processBuilder.directory(executableDir);
			process = processBuilder.start();
			BufferedReader bufferStdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader bufferStderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

			stdoutThread = new Thread() {
				@Override
				public void run() {
					try {
						processStdout(bufferStdout);
					} catch (Exception e) {
						LOGGER.log(Level.WARNING, "Exception encountered reading stdout of command " + command, e);
					}
				}
			};
			stdoutThread.start();
			stderrThread = new Thread() {
				@Override
				public void run() {
					try {
						processStderr(bufferStderr);
					} catch (Exception e) {
						LOGGER.log(Level.WARNING, "Exception encountered reading stderr of command " + command, e);
					}
				}
			};
			stderrThread.start();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Exception encountered while running command " + command, e);
			reset();
		}
	}

	public final void stop() throws InterruptedException {
		if (forceDestroyThread != null) {
			LOGGER.info("Waiting for termination...");
			return;
		}

		forceDestroyThread = new Thread() {
			@Override
			public void run() {
				process.destroy();
				LOGGER.info("Native hook process for " + AbstractNativeHookEventProcessor.this.getName() + " destroyed.");

				try {
					Thread.sleep(TIMEOUT_MS);
				} catch (InterruptedException e) {
					LOGGER.log(Level.WARNING, "Interrupted while waiting for " + getName() + " to terminate", e);
				}

				if (process.isAlive()) {
					LOGGER.info("Forcing " + getName() + " termination");
					process.destroyForcibly();
				}
			}
		};
		forceDestroyThread.start();
		forceDestroyThread.join();
		stdoutThread.join();
		stderrThread.join();
		reset();
	}

	public final boolean isRunning() {
		return forceDestroyThread == null &&
				stdoutThread != null &&
				stderrThread != null &&
				stdoutThread.isAlive() &&
				stderrThread.isAlive();
	}

	private void processStdout(BufferedReader reader) throws IOException {
		String line;
		while ((line = reader.readLine()) != null) {
			String trimmed = line.trim();
			if (trimmed.length() == 0) {
				continue;
			}

			try {
				processStdout(line);
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Exception when processing stdout for " + getName() + ". " + e.getMessage(), e);
			}
		}
	}

	private void processStderr(BufferedReader reader) throws IOException {
		String line;
		while ((line = reader.readLine()) != null) {
			String trimmed = line.trim();
			if (trimmed.length() == 0) {
				continue;
			}

			try {
				processStderr(line);
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Exception when processing stderr for " + getName() + ". " + e.getMessage(), e);
			}
		}
	}

	private void reset() {
		process = null;
		stdoutThread = null;
		stderrThread = null;
		forceDestroyThread = null;
	}
}
