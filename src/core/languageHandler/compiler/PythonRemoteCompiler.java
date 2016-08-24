package core.languageHandler.compiler;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import utilities.FileUtility;
import utilities.Pair;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import core.controller.Core;
import core.ipc.IIPCService;
import core.ipc.IPCServiceManager;
import core.ipc.IPCServiceName;
import core.ipc.repeatClient.PythonIPCClientService;
import core.languageHandler.Language;
import core.userDefinedTask.UserDefinedAction;

public class PythonRemoteCompiler extends AbstractRemoteNativeCompiler {

	private File interpreter;
	private final File objectFileDirectory;

	{
		getLogger().setLevel(Level.ALL);
	}

	public PythonRemoteCompiler(File objectFileDirectory) {
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
	protected Pair<DynamicCompilerOutput, UserDefinedAction> loadAction(final int id, final String source, final File sourceFile) {
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
				Pair<DynamicCompilerOutput, UserDefinedAction> recompiled = PythonRemoteCompiler.this.compile(source);
				UserDefinedAction output = recompiled.getB();
				output.syncContent(this);
				return output;
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

	/*******************************************************************/
	/************************Swing components***************************/
	/*******************************************************************/

	@Override
	public void promptChangePath(JFrame parent) {
		JFileChooser chooser = new JFileChooser(getPath());
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (chooser.showDialog(parent, "Set Python interpreter") == JFileChooser.APPROVE_OPTION) {
			File selectedFile = chooser.getSelectedFile();
			if (!selectedFile.canExecute()) {
				JOptionPane.showMessageDialog(parent,
						"Chosen file " + selectedFile.getName() + " is not executable", "Invalid choice", JOptionPane.WARNING_MESSAGE);
				return;
			}
			setPath(selectedFile);
			((PythonIPCClientService)IPCServiceManager.getIPCService(IPCServiceName.PYTHON)).setExecutingProgram(selectedFile);
		}
	}

	@Override
	public void changeCompilationButton(JButton bCompile) {
		bCompile.setText("Load source");
		File interpreter = getPath();
		getLogger().info("Using python interpreter at " + interpreter.getAbsolutePath());
		IIPCService pythonIPCService = IPCServiceManager.getIPCService(IPCServiceName.PYTHON);

		if (!pythonIPCService.isRunning()) {
			try {
				pythonIPCService.startRunning();
			} catch (IOException e) {
				getLogger().log(Level.WARNING, "Encountered exception launching ipc", e);
			}
		}
	}
}
