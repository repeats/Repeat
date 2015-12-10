package core.languageHandler.compiler;

import java.io.File;
import java.util.logging.Level;

import utilities.FileUtility;
import utilities.Pair;
import utilities.RandomUtil;
import argo.jdom.JsonNode;
import core.ipc.repeatServer.processors.TaskProcessor;
import core.ipc.repeatServer.processors.TaskProcessorManager;
import core.languageHandler.Language;
import core.userDefinedTask.DormantUserDefinedTask;
import core.userDefinedTask.UserDefinedAction;

public abstract class AbstractRemoteNativeCompiler extends AbstractNativeCompiler {

	protected TaskProcessor remoteTaskManager;

	{
		getLogger().setLevel(Level.ALL);
	}

	@Override
	public final Pair<DynamicCompilerOutput, UserDefinedAction> compile(String source) {
		String fileName = getDummyPrefix() + RandomUtil.randomID();
		File sourceFile = getSourceFile(fileName);
		return compile(source, sourceFile);
	}

	@Override
	public final Pair<DynamicCompilerOutput, UserDefinedAction> compile(String source, File objectFile) {
		TaskProcessor remoteManager = TaskProcessorManager.getProcessor(getName());
		if (remoteManager == null) {
			getLogger().warning("Does not have a remote compiler to work with...");
			return new Pair<DynamicCompilerOutput, UserDefinedAction>(DynamicCompilerOutput.COMPILER_MISSING, new DormantUserDefinedTask(source));
		}

		remoteTaskManager = remoteManager;

		try {
			if (!checkRemoteCompilerSettings()) {
				getLogger().warning("Remote compiler check failed! Compilation ended prematurely.");
				return new Pair<DynamicCompilerOutput, UserDefinedAction>(DynamicCompilerOutput.COMPILER_MISCONFIGURED, new DormantUserDefinedTask(source));
			}

			if (!objectFile.getName().endsWith(getObjectExtension())) {
				getLogger().warning("Object file " + objectFile.getAbsolutePath() + "does not end with " + getObjectExtension() + ". Compiling from source code.");
				return compile(source);
			}

			if (!FileUtility.fileExists(objectFile)) {
				if (!FileUtility.writeToFile(source, objectFile, false)) {
					getLogger().warning("Cannot write source code to file " + objectFile.getAbsolutePath());
					return new Pair<DynamicCompilerOutput, UserDefinedAction>(DynamicCompilerOutput.SOURCE_NOT_ACCESSIBLE, new DormantUserDefinedTask(source));
				}
			}

			int id = remoteTaskManager.createTask(objectFile);
			if (id == -1) {
				getLogger().warning("Unable to create task from ipc client...");
				return new Pair<DynamicCompilerOutput, UserDefinedAction>(DynamicCompilerOutput.COMPILATION_ERROR, null);
			}
			return loadAction(id, source, objectFile);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Cannot compile source code...", e);
			return new Pair<DynamicCompilerOutput, UserDefinedAction>(DynamicCompilerOutput.COMPILATION_ERROR, null);
		}
	}

	protected abstract boolean checkRemoteCompilerSettings();

	protected abstract Pair<DynamicCompilerOutput, UserDefinedAction> loadAction(int id, String source, File objectFile);

	@Override
	public abstract Language getName();

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
