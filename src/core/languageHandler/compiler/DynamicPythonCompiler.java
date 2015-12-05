package core.languageHandler.compiler;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import utilities.FileUtility;
import utilities.Pair;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import core.controller.Core;
import core.languageHandler.Language;
import core.userDefinedTask.UserDefinedAction;

public class DynamicPythonCompiler extends AbstractRemoteNativeDynamicCompiler {

	private File interpreter;
	private final File objectFileDirectory;

	{
		getLogger().setLevel(Level.ALL);
	}

	public DynamicPythonCompiler(File objectFileDirectory) {
		interpreter = new File("python.exe");
		this.objectFileDirectory = objectFileDirectory;
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
	protected Pair<DynamicCompilerOutput, UserDefinedAction> loadAction(final int id, final File sourceFile) {
		UserDefinedAction output = new UserDefinedAction() {
			@Override
			public void action(Core controller) {
				boolean result = remoteTaskManager.runTask(id, invokingKeyChain);
				if (!result) {
					getLogger().warning("Unable to run task with id = " + id);
				}
			}
		};
		output.setSourcePath(sourceFile.getAbsolutePath());

		getLogger().info("Successfully loaded action from remote compiler with id = " + id);
		return new Pair<DynamicCompilerOutput, UserDefinedAction>(DynamicCompilerOutput.COMPILATION_SUCCESS, output);
	}

	@Override
	protected File getSourceFile(String compilingAction) {
		return new File(FileUtility.joinPath(objectFileDirectory.getAbsolutePath(), compilingAction + this.getExtension()));
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
		return Logger.getLogger(DynamicPythonCompiler.class.getName());
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
