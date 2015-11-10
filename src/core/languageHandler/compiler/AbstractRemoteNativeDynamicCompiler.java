package core.languageHandler.compiler;

import java.io.File;
import java.util.logging.Level;

import utilities.FileUtility;
import utilities.RandomUtil;
import argo.jdom.JsonNode;
import core.ipc.repeatServer.TaskProcessor;
import core.ipc.repeatServer.TaskProcessorManager;
import core.userDefinedTask.UserDefinedAction;

public abstract class AbstractRemoteNativeDynamicCompiler extends AbstractNativeDynamicCompiler {

	protected TaskProcessor remoteTaskManager;

	{
		getLogger().setLevel(Level.ALL);
	}

	@Override
	public final UserDefinedAction compile(String source) {
		String fileName = getDummyPrefix() + RandomUtil.randomID();
		File sourceFile = getSourceFile(fileName);
		return compile(source, sourceFile);
	}

	@Override
	public final UserDefinedAction compile(String source, File objectFile) {
		if (remoteTaskManager == null) {
			TaskProcessor remoteManager = TaskProcessorManager.getProcessor(getName());
			if (remoteManager == null) {
				getLogger().warning("Does not have a remote compiler to work with...");
				return null;
			} else {
				remoteTaskManager = remoteManager;
			}
		}

		try {
			if (!checkRemoteCompilerSettings()) {
				getLogger().warning("Remote compiler check failed! Compilation ended prematurely.");
				return null;
			}

			if (!objectFile.getName().endsWith(getObjectExtension())) {
				getLogger().warning("Object file " + objectFile.getAbsolutePath() + "does not end with " + getObjectExtension() + ". Compiling from source code.");
				return compile(source);
			}

			if (!FileUtility.fileExists(objectFile)) {
				if (!FileUtility.writeToFile(source, objectFile, false)) {
					getLogger().warning("Cannot write source code to file " + objectFile.getAbsolutePath());
					return null;
				}
			}

			int id = remoteTaskManager.createTask(objectFile);
			if (id == -1) {
				getLogger().warning("Unable to create task from ipc client...");
				return null;
			}
			return loadAction(id, objectFile);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Cannot compile source code...", e);
			return null;
		}
	}

	protected abstract boolean checkRemoteCompilerSettings();

	protected abstract UserDefinedAction loadAction(int id, File objectFile);

	@Override
	public abstract String getName();

	@Override
	public abstract String getExtension();

	@Override
	public abstract String getObjectExtension();

	@Override
	public abstract File getPath();

	@Override
	public abstract void setPath(File path);

	@Override
	public abstract boolean parseCompilerSpecificArgs(JsonNode node);

	@Override
	public abstract JsonNode getCompilerSpecificArgs();

	@Override
	protected abstract File getSourceFile(String compilingAction);

	@Override
	protected abstract String getDummyPrefix();
}
