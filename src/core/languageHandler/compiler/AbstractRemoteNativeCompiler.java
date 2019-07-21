package core.languageHandler.compiler;

import java.io.File;
import java.util.logging.Level;

import argo.jdom.JsonNode;
import core.controller.Core;
import core.ipc.repeatServer.processors.TaskProcessor;
import core.ipc.repeatServer.processors.TaskProcessorManager;
import core.languageHandler.Language;
import core.userDefinedTask.DormantUserDefinedTask;
import core.userDefinedTask.UserDefinedAction;
import utilities.FileUtility;
import utilities.Pair;
import utilities.RandomUtil;

public abstract class AbstractRemoteNativeCompiler extends AbstractNativeCompiler {

	protected TaskProcessor remoteTaskManager;
	protected File objectFileDirectory;

	{
		getLogger().setLevel(Level.ALL);
	}

	public AbstractRemoteNativeCompiler(File objectFileDirectory) {
		this.objectFileDirectory = objectFileDirectory;
	}

	@Override
	public final Pair<DynamicCompilerOutput, UserDefinedAction> compile(String source) {
		String fileName = getDummyPrefix() + RandomUtil.randomID();
		File sourceFile = getSourceFile(fileName);
		return compile(source, sourceFile);
	}

	@Override
	public final Pair<DynamicCompilerOutput, UserDefinedAction> compile(String source, File sourceFile) {
		TaskProcessor remoteManager = TaskProcessorManager.getProcessor(getName());
		if (remoteManager == null) {
			getLogger().warning("Does not have a remote compiler to work with " + getName());
			return new Pair<DynamicCompilerOutput, UserDefinedAction>(DynamicCompilerOutput.COMPILER_MISSING, new DormantUserDefinedTask(source));
		}

		remoteTaskManager = remoteManager;

		try {
			if (!checkRemoteCompilerSettings()) {
				getLogger().warning("Remote compiler check failed! Compilation ended prematurely.");
				return new Pair<DynamicCompilerOutput, UserDefinedAction>(DynamicCompilerOutput.COMPILER_MISCONFIGURED, new DormantUserDefinedTask(source));
			}

			if (!sourceFile.getName().endsWith(getExtension())) {
				getLogger().warning("Source file " + sourceFile.getAbsolutePath() + " does not end with " + getExtension() + ". Compiling from source code.");
				return compile(source);
			}

			if (!FileUtility.fileExists(sourceFile)) {
				if (!FileUtility.writeToFile(source, sourceFile, false)) {
					getLogger().warning("Cannot write source code to file " + sourceFile.getAbsolutePath());
					return new Pair<DynamicCompilerOutput, UserDefinedAction>(DynamicCompilerOutput.SOURCE_NOT_ACCESSIBLE, new DormantUserDefinedTask(source));
				}
			}

			String id = remoteTaskManager.createTask(sourceFile);
			if (id.isEmpty()) {
				getLogger().warning("Unable to create task from ipc client...");
				return new Pair<DynamicCompilerOutput, UserDefinedAction>(DynamicCompilerOutput.COMPILATION_ERROR, null);
			}
			return loadAction(id, source, sourceFile);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Cannot compile source code...", e);
			return new Pair<DynamicCompilerOutput, UserDefinedAction>(DynamicCompilerOutput.COMPILATION_ERROR, null);
		}
	}

	protected abstract boolean checkRemoteCompilerSettings();

	protected Pair<DynamicCompilerOutput, UserDefinedAction> loadAction(final String id, final String source, File objectFile) {
		UserDefinedAction output = new UserDefinedAction() {
			@Override
			public void action(Core controller) {
				boolean result = remoteTaskManager.runTask(id, invoker);
				if (!result) {
					getLogger().warning("Unable to run task with id = " + id);
				}
			}

			@Override
			public UserDefinedAction recompile(AbstractNativeCompiler compiler, boolean clean) {
				Pair<DynamicCompilerOutput, UserDefinedAction> recompiled = compile(source);
				if (recompiled.getA() != DynamicCompilerOutput.COMPILATION_SUCCESS) {
					getLogger().warning("Unable to recompile task id = " + id + ". Error is " + recompiled.getA());
					return null;
				}

				UserDefinedAction output = recompiled.getB();
				output.syncContent(this);
				return output;
			}
		};
		output.setSourcePath(objectFile.getAbsolutePath());

		getLogger().info("Successfully loaded action from remote compiler with id = " + id);
		return new Pair<DynamicCompilerOutput, UserDefinedAction>(DynamicCompilerOutput.COMPILATION_SUCCESS, output);
	}

	@Override
	protected final File getSourceFile(String compilingAction) {
		return new File(FileUtility.joinPath(objectFileDirectory.getAbsolutePath(), compilingAction + this.getExtension()));
	}

	@Override
	public abstract Language getName();

	@Override
	public abstract String getExtension();

	@Override
	public abstract String getObjectExtension();

	@Override
	public abstract File getPath();

	@Override
	public abstract boolean setPath(File path);

	@Override
	public abstract boolean parseCompilerSpecificArgs(JsonNode node);

	@Override
	public abstract JsonNode getCompilerSpecificArgs();

	@Override
	protected abstract String getDummyPrefix();
}
