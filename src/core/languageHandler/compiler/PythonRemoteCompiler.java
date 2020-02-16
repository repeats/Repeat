package core.languageHandler.compiler;

import java.io.File;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import core.languageHandler.Language;
import utilities.FileUtility;

public class PythonRemoteCompiler extends AbstractRemoteNativeCompiler {

	private File interpreter;

	{
		getLogger().setLevel(Level.ALL);
	}

	public PythonRemoteCompiler(File objectFileDirectory) {
		super(objectFileDirectory);
		interpreter = new File("python.exe");
	}

	@Override
	public boolean canSetPath() {
		return true;
	}

	@Override
	public boolean setPath(File file) {
		if (Files.isExecutable(file.toPath())) {
			interpreter = file;
			return true;
		}
		getLogger().warning("Python interpreter must be an executable.");
		return false;
	}

	@Override
	public File getPath() {
		return interpreter;
	}

	@Override
	public Language getName() {
		return Language.PYTHON;
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
	public boolean parseCompilerSpecificArgs(JsonNode node) {
		return true;
	}

	@Override
	public JsonNode getCompilerSpecificArgs() {
		return JsonNodeFactories.object();
	}

	@Override
	protected String getDummyPrefix() {
		return "PY_";
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(PythonRemoteCompiler.class.getName());
	}

	@Override
	protected boolean checkRemoteCompilerSettings() {
		if (!FileUtility.fileExists(interpreter) || !interpreter.canExecute()) {
			getLogger().severe("No interpreter found at " + interpreter.getAbsolutePath());
			return false;
		}

		return true;
	}
}
