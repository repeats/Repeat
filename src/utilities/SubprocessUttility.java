package utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SubprocessUttility {

	private static final Logger LOGGER = Logger.getLogger(SubprocessUttility.class.getName());

	/**
	 * Execute a command in the runtime environment
	 * @param command The command to execute
	 * @param cwd directory in which the command should be executed. Set null to execute in the current directory
	 * @return stdout of the command, or empty string if there is any exception encountered.
	 */
	public static String execute(String command, File cwd) {
		try {
			StringBuffer output = new StringBuffer();
			Process process = Runtime.getRuntime().exec(command, null, cwd);
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;

			while ((line = input.readLine()) != null) {
				String trimmed = line.trim();
				if (trimmed.length() == 0) {
					continue;
				}
				output.append(trimmed);
				output.append("\n");
			}

			process.waitFor();
			return output.toString();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Exception encountered while running command " + command, e);
			return "";
		}
	}

	/**
	 * Execute a command in the runtime environment
	 * @param command The command to execute
	 * @return stdout of the command, or empty string if there is any exception encountered.
	 */
	public static String execute(String command) {
		return execute(command, null);
	}

	private SubprocessUttility() {}
}
