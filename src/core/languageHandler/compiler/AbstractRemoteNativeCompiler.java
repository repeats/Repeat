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
	public final DynamicCompilationResult compile(String source) {
		String fileName = getDummyPrefix() + RandomUtil.randomID();
		File sourceFile = getSourceFile(fileName);
		return compile(source, sourceFile);
	}

	@Override
	public final DynamicCompilationResult compile(String source, File sourceFile) {
		TaskProcessor remoteManager = TaskProcessorManager.getProcessor(getName());
		if (remoteManager == null) {
			getLogger().warning("Does not have a remote compiler to work with " + getName());
			return DynamicCompilationResult.of(DynamicCompilerOutput.COMPILER_MISSING, new DormantUserDefinedTask(source, getName()));
		}

		remoteTaskManager = remoteManager;

		try {
			if (!checkRemoteCompilerSettings()) {
				getLogger().warning("Remote compiler check failed! Compilation ended prematurely.");
				return DynamicCompilationResult.of(DynamicCompilerOutput.COMPILER_MISCONFIGURED, new DormantUserDefinedTask(source, getName()));
			}

			if (!sourceFile.getName().endsWith(getExtension())) {
				getLogger().warning("Source file " + sourceFile.getAbsolutePath() + " does not end with " + getExtension() + ". Compiling from source code.");
				return compile(source);
			}

			if (!FileUtility.fileExists(sourceFile)) {
				if (!FileUtility.writeToFile(source, sourceFile, false)) {
					getLogger().warning("Cannot write source code to file " + sourceFile.getAbsolutePath());
					return DynamicCompilationResult.of(DynamicCompilerOutput.SOURCE_NOT_ACCESSIBLE, new DormantUserDefinedTask(source, getName()));
				}
			}

			String id = remoteTaskManager.createTask(sourceFile);
			if (id.isEmpty()) {
				getLogger().warning("Unable to create task from ipc client...");
				return DynamicCompilationResult.of(DynamicCompilerOutput.COMPILATION_ERROR, null);
			}
			return loadAction(id, source, sourceFile);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Cannot compile source code...", e);
			return DynamicCompilationResult.of(DynamicCompilerOutput.COMPILATION_ERROR, null);
		}
	}

	protected abstract boolean checkRemoteCompilerSettings();

	protected final DynamicCompilationResult loadAction(final String id, final String source, File objectFile) {
		UserDefinedAction output = new UserDefinedAction() {
			@Override
			public void action(Core controller) {
				boolean result = remoteTaskManager.runTask(id, invoker);
				if (!result) {
					getLogger().warning("Unable to run task with id = " + id);
				}
			}

			@Override
			public UserDefinedAction recompileNative(AbstractNativeCompiler compiler) {
				DynamicCompilationResult recompiled = compile(source);
				if (recompiled.output() != DynamicCompilerOutput.COMPILATION_SUCCESS) {
					getLogger().warning("Unable to recompile task id = " + id + ". Error is " + recompiled.output());
					return null;
				}

				UserDefinedAction output = recompiled.action();
				output.syncContent(this);
				return output;
			}
		};
		output.setSourcePath(objectFile.getAbsolutePath());

		getLogger().info("Successfully loaded action from remote compiler with id = " + id);
		return DynamicCompilationResult.of(DynamicCompilerOutput.COMPILATION_SUCCESS, output);
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
