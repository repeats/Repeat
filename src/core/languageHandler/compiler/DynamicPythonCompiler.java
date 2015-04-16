package core.languageHandler.compiler;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.logging.Level;
import java.util.logging.Logger;

import utilities.FileUtility;
import utilities.RandomUtil;
import utilities.logging.ExceptionUtility;
import core.controller.Core;
import core.userDefinedTask.UserDefinedAction;

public class DynamicPythonCompiler implements DynamicCompiler {

	private static final Logger LOGGER = Logger.getLogger(DynamicPythonCompiler.class.getName());
	private static final String DUMMY_PREFIX = "PY_";
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
	public UserDefinedAction compile(String source, File objectFile) {
		try {
			if (!FileUtility.fileExists(interpreter) || !interpreter.canExecute()) {
				LOGGER.severe("No interpreter found at " + interpreter.getAbsolutePath());
				return null;
			}

			if (!objectFile.getName().endsWith(".py")) {
				LOGGER.warning("Python object file " + objectFile.getAbsolutePath() + "does not end with .py. Compiling from source code.");
				return compile(source);
			}

			if (FileUtility.fileExists(objectFile)) {
				return loadAction(objectFile);
			} else {
				if (FileUtility.writeToFile(source, objectFile, false)) {
					return loadAction(objectFile);
				} else {
					LOGGER.warning("Cannot write source code to file " + objectFile.getAbsolutePath());
					return null;
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Cannot compile source code...", e);
			return null;
		}
	}

	@Override
	public UserDefinedAction compile(String source) {
		String fileName = DUMMY_PREFIX + RandomUtil.randomID();
		File sourceFile = new File("core/" + fileName + ".py");
		return compile(source, sourceFile);
	}

	private UserDefinedAction loadAction(final File sourceFile) {
		UserDefinedAction output = new UserDefinedAction() {
			@Override
			public void action(Core controller) {
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
				} catch (Exception e) {
					LOGGER.log(Level.WARNING, "Execution encountered error...", e);
				}
			}
		};
		output.setSourcePath(sourceFile.getAbsolutePath());
		return output;
	}

	@Override
	public String getName() {
		return "python";
	}

	@Override
	public String getExtension() {
		return ".py";
	}

	@Override
	public String getObjectExtension() {
		return ".py";
	}

	@Override
	public String getRunArgs() {
		return "";
	}

	@Override
	public void setRunArgs(String args) {
	}

}
