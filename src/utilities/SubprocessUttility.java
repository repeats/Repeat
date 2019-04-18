package utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SubprocessUttility {

	private static final Logger LOGGER = Logger.getLogger(SubprocessUttility.class.getName());

	/**
	 * Execute a command in the runtime environment
	 * @param command The command to execute
	 * @param cwd directory in which the command should be executed. Set null or empty string to execute in the current directory
	 * @return stdout and stderr of the command
	 * @throws ExecutionException if there is any exception encountered.
	 */
	public static String[] execute(String command, String cwd) throws ExecutionException {
		File dir = null;
		if (cwd != null && !cwd.isEmpty()) {
			dir = new File(cwd);
		}

		// 0 for stdout, 1 for stderr.
		final boolean[] fail = new boolean[2];

		try {
			StringBuffer stdout = new StringBuffer();
			StringBuffer stderr = new StringBuffer();
			Process process = Runtime.getRuntime().exec(command, null, dir);
			BufferedReader bufferStdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader bufferStderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

			Thread t1 = new Thread() {
				@Override
				public void run() {
					try {
						readFromStream(bufferStdout, stdout);
					} catch (Exception e) {
						LOGGER.log(Level.WARNING, "Exception encountered reading stdout of command $" + command, e);
						fail[0] = true;
					}
				}
			};
			t1.start();
			Thread t2 = new Thread() {
				@Override
				public void run() {
					try {
						readFromStream(bufferStderr, stderr);
					} catch (Exception e) {
						LOGGER.log(Level.WARNING, "Exception encountered reading stderr of command $" + command, e);
						fail[1] = true;
					}
				}
			};
			t2.start();
			t1.join();
			t2.join();

			process.waitFor();

			if (fail[0] || fail[1]) {
				LOGGER.log(Level.WARNING, "Exception encountered when executing command $" + command);
				throw new ExecutionException();
			}

			return new String[] {stdout.toString(), stderr.toString()};
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Exception encountered while running command " + command, e);
			throw new ExecutionException();
		}
	}

	private static void readFromStream(BufferedReader reader, StringBuffer output) throws IOException {
		String line;
		while ((line = reader.readLine()) != null) {
			String trimmed = line.trim();
			if (trimmed.length() == 0) {
				continue;
			}
			output.append(trimmed);
			output.append("\n");
		}
	}

	/**
	 * Execute a command in the runtime environment
	 * @param command The command to execute
	 * @param cwd directory in which the command should be executed. Set null to execute in the current directory
	 * @return stdout of the command, or empty string if there is any exception encountered.
	 */
	public static String execute(String command, File cwd) throws ExecutionException {
		String path = null;
		if (cwd != null) {
			path = cwd.getPath();
		}

		return execute(command, path)[0];
	}

	/**
	 * Execute a command in the runtime environment
	 * @param command The command to execute
	 * @return stdout of the command, or empty string if there is any exception encountered.
	 */
	public static String execute(String command) throws ExecutionException {
		return execute(command, "")[0];
	}

	public static class ExecutionException extends Exception {
		private static final long serialVersionUID = 6688739122137565700L;
		private ExecutionException() {}
	}

	private SubprocessUttility() {}
}
