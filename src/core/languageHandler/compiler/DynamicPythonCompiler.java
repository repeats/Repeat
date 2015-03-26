package core.languageHandler.compiler;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.logging.Level;
import java.util.logging.Logger;

import utilities.ExceptionUtility;
import utilities.FileUtility;
import core.UserDefinedAction;
import core.controller.Core;

public class DynamicPythonCompiler implements DynamicCompiler {

	private static final Logger LOGGER = Logger.getLogger(DynamicPythonCompiler.class.getName());
	private File interpreter;

	static {
		LOGGER.setLevel(Level.ALL);
	}

	public DynamicPythonCompiler() {
		interpreter = new File("python.exe");
	}

	@Override
	public void setPath(File file) {
		interpreter = file;
	}

	@Override
	public File getPath() {
		return interpreter;
	}

	@Override
	public UserDefinedAction compile(final String source) {
		if (!FileUtility.fileExists(interpreter)) {
			LOGGER.severe("No interpreter found at " + interpreter.getAbsolutePath());
			return null;
		}

		return new UserDefinedAction() {
			@Override
			public void action(Core controller) {
				File sourceFile = new File("custom_action.py");
				FileUtility.writeToFile(source, sourceFile, false);

				String[] cmd = { interpreter.getAbsolutePath(), "-u", sourceFile.getPath() };
				ProcessBuilder pb = new ProcessBuilder(cmd);
				pb.redirectOutput(Redirect.INHERIT);
				pb.redirectError(Redirect.INHERIT);

		        Process p = null;
				try {
					p = pb.start();

					p.waitFor();
				} catch (IOException e) {
					LOGGER.warning(ExceptionUtility.getStackTrace(e));
				} catch (InterruptedException e) {
					if (p != null) {
						p.destroy();
					}
					LOGGER.info("Task ended prematurely");
				}
			}
		};
	}

	@Override
	public String getName() {
		return "python";
	}

	@Override
	public String getRunArgs() {
		return "";
	}

	@Override
	public void setRunArgs(String args) {
		// TODO Auto-generated method stub

	}
}
