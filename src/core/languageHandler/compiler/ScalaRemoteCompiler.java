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
import core.ipc.repeatClient.ScalaIPCClientService;
import core.languageHandler.Language;

public class ScalaRemoteCompiler extends AbstractRemoteNativeCompiler {

	{
		getLogger().setLevel(Level.ALL);
	}

	public ScalaRemoteCompiler(File objectFileDirectory) {
		super(objectFileDirectory);
	}

	@Override
	protected boolean checkRemoteCompilerSettings() {
		return true;
	}

	@Override
	public Language getName() {
		return Language.SCALA;
	}

	@Override
	public String getExtension() {
		return ".scala";
	}

	@Override
	public String getObjectExtension() {
		return ".class";
	}

	@Override
	public File getPath() {
		return new File(".");
	}

	@Override
	public boolean canSetPath() {
		return false;
	}

	@Override
	public boolean setPath(File path) {
		// Intentionally left blank
		return false;
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
		return "SCALA_";
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(getClass().getName());
	}

	@Override
	public void promptChangePath(JFrame parent) {
		ScalaIPCClientService ipcService = ((ScalaIPCClientService)IPCServiceManager.getIPCService(IPCServiceName.SCALA));

		JFileChooser chooser = new JFileChooser(getPath());
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		// First select the JRE to launch scala subsystem
		if (chooser.showDialog(parent, "Set JRE") == JFileChooser.APPROVE_OPTION) {
			File selectedFile = chooser.getSelectedFile();
			if (!selectedFile.canExecute()) {
				JOptionPane.showMessageDialog(parent,
						"Chosen file " + selectedFile.getName() + " is not executable", "Invalid choice", JOptionPane.WARNING_MESSAGE);
				return;
			}
			setPath(selectedFile);
			ipcService.setExecutingProgram(selectedFile);
		}

		// Next select the scala jar directories containing the jar dependencies for the subsystem
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (chooser.showDialog(parent, "Set scala jar library directory") == JFileChooser.APPROVE_OPTION) {
			File selectedFile = chooser.getSelectedFile();
			if (!selectedFile.isDirectory()) {
				JOptionPane.showMessageDialog(parent,
						"Chosen path " + selectedFile.getName() + " is not a directory", "Invalid choice", JOptionPane.WARNING_MESSAGE);
				return;
			}

			ipcService.setScalaLibraryDirectory(selectedFile);
		}
	}

	@Override
	public void changeCompilationButton(JButton bCompile) {
		// Nothing to do at the moment.
	}

	@Override
	public void configure() {
		// Nothing to do at the moment
	}
}
