package core.languageHandler.compiler;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import utilities.FileUtility;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import core.controller.Core;
import core.ipc.client.AbstractIPCClient;
import core.userDefinedTask.UserDefinedAction;

public class DynamicPythonCompiler extends AbstractRemoteNativeDynamicCompiler {

	private File interpreter;
	private final File objectFileDirectory;

	{
		getLogger().setLevel(Level.ALL);
	}

	public DynamicPythonCompiler(AbstractIPCClient ipcClient, File objectFileDirectory) {
		super(ipcClient);
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
	protected UserDefinedAction loadAction(final int id, final File sourceFile) {
		UserDefinedAction output = new UserDefinedAction() {
			@Override
			public void action(Core controller) {
				boolean result = ipcClient.runTask(id, invokingKeyChain);
				if (!result) {
					getLogger().warning("Unable to run task with id = " + id);
				}
			}
		};
		output.setSourcePath(sourceFile.getAbsolutePath());
		return output;
	}

	@Override
	protected File getSourceFile(String compilingAction) {
		return new File(FileUtility.joinPath(objectFileDirectory.getAbsolutePath(), compilingAction + this.getExtension()));
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
