package core;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import utilities.FileUtility;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.config.IJsonable;
import core.controller.Core;
import core.languageHandler.compiler.DynamicCompiler;
import core.languageHandler.compiler.DynamicCompilerFactory;

public abstract class UserDefinedAction implements IJsonable {

	protected String name;
	protected int hotkey;
	protected String sourcePath;
	protected String compilerName;

	public abstract void action(Core controller) throws InterruptedException;

	public void setName(String name) {
		this.name = name;
	}

	public void setHotkey(int hotkey) {
		this.hotkey = hotkey;
	}

	public String getName() {
		return name;
	}

	public int getHotkey() {
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

	/***********************************************************************/
	@Override
	public final JsonRootNode jsonize() {
		return JsonNodeFactories.object(
				JsonNodeFactories.field("source_path", JsonNodeFactories.string(sourcePath)),
				JsonNodeFactories.field("compiler", JsonNodeFactories.string(compilerName)),
				JsonNodeFactories.field("name", JsonNodeFactories.string(name)),
				JsonNodeFactories.field("hotkey", JsonNodeFactories.number(hotkey))
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
			int hotkey = Integer.parseInt(node.getNumberValue("hotkey"));

			StringBuffer sourceBuffer = FileUtility.readFromFile(new File(sourcePath));
			String source = null;
			if (sourceBuffer == null) {
				JOptionPane.showMessageDialog(null, "Cannot get source at path " + sourcePath);
				return null;
			} else {
				source = sourceBuffer.toString();
			}

			UserDefinedAction output = compiler.compile(source);
			if (output == null) {
				JOptionPane.showMessageDialog(null, "Compilation failed for task " + name + " with source at path " + sourcePath);
				return null;
			}

			output.sourcePath = sourcePath;
			output.compilerName = compiler.getName();
			output.name = name;
			output.hotkey = hotkey;

			return output;
		} catch (Exception e) {
			Logger.getLogger(FileUtility.class.getName()).log(Level.WARNING, "Exception parsing task from JSON", e);
			return null;
		}
	}

}