package core;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.logging.Level;
import java.util.logging.Logger;

import utilities.ExceptionUtility;
import utilities.FileUtility;

public class DynamicPythonCompiler implements DynamicCompiler {

	private static final Logger LOGGER = Logger.getLogger(DynamicPythonCompiler.class.getName());
	private File interpreter;

	static {
		LOGGER.setLevel(Level.ALL);
	}

	public DynamicPythonCompiler() {
		interpreter = new File("python.exe");
	}

	public void setInterpreter(File file) {
		interpreter = file;
	}

	public File getInterpreter() {
		return interpreter;
	}

	@Override
	public UserDefinedAction compile(final String source) {
		return new UserDefinedAction() {
			@Override
			public void action(Core controller) {
				File sourceFile = new File("custom_action.py");
				FileUtility.writeToFile(new StringBuffer(source), sourceFile, false);


				String[] cmd = { interpreter.getAbsolutePath(), sourceFile.getPath() };
				ProcessBuilder pb = new ProcessBuilder(cmd);
				pb.redirectOutput(Redirect.INHERIT);
				pb.redirectError(Redirect.INHERIT);

		        Process p;
				try {
					p = pb.start();

					p.waitFor();
				} catch (InterruptedException | IOException e) {
					LOGGER.warning(ExceptionUtility.getStackTrace(e));
				}
			}
		};
	}

	@Override
	public String getName() {
		return "python";
	}
}
