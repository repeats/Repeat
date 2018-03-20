package core.languageHandler.compiler;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import core.ipc.IPCServiceManager;
import core.ipc.IPCServiceName;
import core.ipc.repeatClient.PythonIPCClientService;
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
	public void setPath(File file) {
		interpreter = file;
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
		File interpreter = getPath();
		getLogger().info("Using python interpreter at " + interpreter.getAbsolutePath());
	}

	@Override
	public void configure() {
		// Nothing to do at the moment
	}
}
