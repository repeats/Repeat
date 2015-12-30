package core.languageHandler.compiler;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import utilities.FileUtility;
import utilities.Pair;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import core.controller.Core;
import core.languageHandler.Language;
import core.userDefinedTask.UserDefinedAction;

public class CSharpRemoteCompiler extends AbstractRemoteNativeCompiler {

	private final File objectFileDirectory;
	private File path;

	{
		getLogger().setLevel(Level.ALL);
	}

	public CSharpRemoteCompiler(File objectFileDirectory) {
		this.objectFileDirectory = objectFileDirectory;
		path = new File(".");
	}

	@Override
	public void setPath(File file) {
		this.path = file;
	}

	@Override
	public File getPath() {
		return path;
	}

	@Override
	protected Pair<DynamicCompilerOutput, UserDefinedAction> loadAction(final int id, final String source, final File sourceFile) {
		UserDefinedAction output = new UserDefinedAction() {
			@Override
			public void action(Core controller) {
				boolean result = remoteTaskManager.runTask(id, invokingKeyChain);
				if (!result) {
					getLogger().warning("Unable to run task with id = " + id);
				}
			}

			@Override
			public UserDefinedAction recompile(AbstractNativeCompiler compiler, boolean clean) {
				Pair<DynamicCompilerOutput, UserDefinedAction> recompiled = CSharpRemoteCompiler.this.compile(source);
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
		return Language.CSHARP;
	}

	@Override
	public String getExtension() {
		return ".cs";
	}

	@Override
	public String getObjectExtension() {
		return ".dll";
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
		return "CS_";
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(CSharpRemoteCompiler.class.getName());
	}

	@Override
	protected boolean checkRemoteCompilerSettings() {
		return true;
	}

	/*******************************************************************/
	/************************Swing components***************************/
	/*******************************************************************/

	@Override
	public void promptChangePath(JFrame parent) {
		JOptionPane.showMessageDialog(parent, "Operation not supported", "C# path", JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public void changeCompilationButton(JButton bCompile) {
		bCompile.setText("Compile source");
	}
}
