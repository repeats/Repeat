package core.userDefinedTask;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import utilities.ExceptableFunction;
import utilities.FileUtility;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.config.IJsonable;
import core.controller.Core;
import core.keyChain.KeyChain;
import core.languageHandler.compiler.DynamicCompiler;
import core.languageHandler.compiler.DynamicCompilerFactory;

public abstract class UserDefinedAction implements IJsonable {

	protected String name;
	protected KeyChain hotkey;
	protected String sourcePath;
	protected String compilerName;
	protected boolean enabled;
	protected ExceptableFunction<Integer, Void, InterruptedException> executeTaskInGroup;

	public UserDefinedAction() {
		enabled = true;
	}

	/**
	 * Custom action defined by user
	 * @param controller See {@link core.controller.Core} class
	 * @throws InterruptedException
	 */
	public abstract void action(Core controller) throws InterruptedException;

	public void setName(String name) {
		this.name = name;
	}

	public void setHotkey(KeyChain hotkey) {
		this.hotkey = hotkey;
	}

	public String getName() {
		return name;
	}

	public KeyChain getHotkey() {
		if (hotkey == null) {
			hotkey = new KeyChain();
		}
		return hotkey;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public String getCompiler() {
		return compilerName;
	}

	public void setCompiler(String compiler) {
		this.compilerName = compiler;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setExecuteTaskInGroup(ExceptableFunction<Integer, Void, InterruptedException> executeTaskInGroup) {
		this.executeTaskInGroup = executeTaskInGroup;
	}

	/***********************************************************************/
	@Override
	public final JsonRootNode jsonize() {
		return JsonNodeFactories.object(
				JsonNodeFactories.field("source_path", JsonNodeFactories.string(sourcePath)),
				JsonNodeFactories.field("compiler", JsonNodeFactories.string(compilerName)),
				JsonNodeFactories.field("name", JsonNodeFactories.string(name)),
				JsonNodeFactories.field("hotkey", getHotkey().jsonize()),
				JsonNodeFactories.field("enabled", JsonNodeFactories.booleanNode(enabled))
				);

	}

	public static UserDefinedAction parseJSON(DynamicCompilerFactory factory, JsonNode node) {
		try {
			String sourcePath = node.getStringValue("source_path");
			DynamicCompiler compiler = factory.getCompiler(node.getStringValue("compiler"));
			if (compiler == null) {
				JOptionPane.showMessageDialog(null, "Unknown compiler " + node.getStringValue("compiler"));
				return null;
			}

			String name = node.getStringValue("name");
			KeyChain hotkey = KeyChain.parseJSON(node.getArrayNode("hotkey"));

			File sourceFile = new File(sourcePath);
			StringBuffer sourceBuffer = FileUtility.readFromFile(sourceFile);
			String source = null;
			if (sourceBuffer == null) {
				JOptionPane.showMessageDialog(null, "Cannot get source at path " + sourcePath);
				return null;
			} else {
				source = sourceBuffer.toString();
			}

			File objectFile = new File(FileUtility.joinPath("core", FileUtility.removeExtension(sourceFile).getName()));
			objectFile = FileUtility.addExtension(objectFile, compiler.getObjectExtension());
			UserDefinedAction output = compiler.compile(source, objectFile);
			if (output == null) {
				JOptionPane.showMessageDialog(null, "Compilation failed for task " + name + " with source at path " + sourcePath);
				return null;
			}

			boolean enabled = node.getBooleanValue("enabled");

			output.sourcePath = sourcePath;
			output.compilerName = compiler.getName();
			output.name = name;
			output.hotkey = hotkey;
			output.enabled = enabled;

			return output;
		} catch (Exception e) {
			Logger.getLogger(UserDefinedAction.class.getName()).log(Level.WARNING, "Exception parsing task from JSON", e);
			return null;
		}
	}

}