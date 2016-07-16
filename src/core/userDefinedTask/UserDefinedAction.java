package core.userDefinedTask;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import utilities.FileUtility;
import utilities.Function;
import utilities.IJsonable;
import utilities.ILoggable;
import utilities.JSONUtility;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.controller.Core;
import core.keyChain.KeyChain;
import core.languageHandler.Language;
import core.languageHandler.compiler.AbstractNativeCompiler;
import core.languageHandler.compiler.DynamicCompilerManager;

public abstract class UserDefinedAction implements IJsonable, ILoggable {

	private static final Logger LOGGER = Logger.getLogger(UserDefinedAction.class.getName());

	protected String name;
	protected Set<KeyChain> hotkeys;
	protected String sourcePath;
	protected Language compiler;
	protected boolean enabled;
	protected KeyChain invokingKeyChain;
	protected UsageStatistics statistics;

	public UserDefinedAction() {
		invokingKeyChain = new KeyChain();
		statistics = new UsageStatistics();
		enabled = true;
	}

	/**
	 * Custom action defined by user
	 * @param controller See {@link core.controller.Core} class
	 * @throws InterruptedException
	 */
	public abstract void action(Core controller) throws InterruptedException;

	/**
	 * Perform the action and track the statistics related to this action.
	 * @param controller
	 * @throws InterruptedException
	 */
	public final void trackedAction(Core controller) throws InterruptedException {
		long time = System.currentTimeMillis();
		statistics.useNow();
		action(controller);
		time = System.currentTimeMillis() - time;
		statistics.updateAverageExecutionTime(time);
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final void setHotKeys(Set<KeyChain> hotkeys) {
		this.hotkeys = hotkeys;
	}

	public final Set<KeyChain> getHotkeys() {
		if (hotkeys == null) {
			hotkeys = new HashSet<KeyChain>();
		}
		return hotkeys;
	}

	/**
	 * Retrieve a random key chain from the set of key chains. If there's no keychain for the task, return an empty key chain.
	 * @return a random key chain from the set of key chains.
	 */
	public final KeyChain getRepresentativeHotkey() {
		Set<KeyChain> hotkeys = getHotkeys();
		if (hotkeys == null || hotkeys.isEmpty()) {
			return new KeyChain();
		} else {
			return hotkeys.iterator().next();
		}
	}

	public final String getName() {
		return name;
	}

	public final String getSourcePath() {
		return sourcePath;
	}

	public String getSource() {
		StringBuffer source = FileUtility.readFromFile(sourcePath);
		if (source == null) {
			return null;
		}

		return source.toString();
	}

	public final void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public final Language getCompiler() {
		return compiler;
	}

	public final void setCompiler(Language compiler) {
		this.compiler = compiler;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public final UsageStatistics getStatistics() {
		return statistics;
	}

	public final void override(UserDefinedAction other) {
		setName(other.getName());
		setHotKeys(other.getHotkeys());
		this.statistics = other.statistics;
	}

	/**
	 * This method is called to dynamically allow the current task to determine which key chain activated it among
	 * its hotkeys. This will only change the key chain definition of the current key chain, not substituting the real object
	 * @param invokingKeyChain
	 */
	public final void setInvokingKeyChain(KeyChain invokingKeyChain) {
		this.invokingKeyChain.getKeys().clear();
		this.invokingKeyChain.getKeys().addAll(invokingKeyChain.getKeys());
	}

	/***********************************************************************/
	public UserDefinedAction recompile(AbstractNativeCompiler compiler, boolean clean) {
		if (!clean) {
			return this;
		} else {
			//TODO recompile the current task
			getLogger().warning("Not supported");
			return null;
		}
	}

	public final void syncContent(UserDefinedAction other) {
		this.sourcePath = other.sourcePath;
		this.compiler = other.compiler;
		this.name = other.name;
		this.hotkeys = other.hotkeys;
		this.enabled = other.enabled;
	}

	/***********************************************************************/
	@Override
	public final JsonRootNode jsonize() {
		return JsonNodeFactories.object(
				JsonNodeFactories.field("source_path", JsonNodeFactories.string(sourcePath)),
				JsonNodeFactories.field("compiler", JsonNodeFactories.string(compiler.toString())),
				JsonNodeFactories.field("name", JsonNodeFactories.string(name)),
				JsonNodeFactories.field("hotkey", JsonNodeFactories.array(JSONUtility.listToJson(getHotkeys()))),
				JsonNodeFactories.field("enabled", JsonNodeFactories.booleanNode(enabled)),
				JsonNodeFactories.field("statistics", statistics.jsonize())
				);
	}

	public static final UserDefinedAction parseJSON(DynamicCompilerManager factory, JsonNode node) {
		try {
			String sourcePath = node.getStringValue("source_path");
			AbstractNativeCompiler compiler = factory.getCompiler(node.getStringValue("compiler"));
			if (compiler == null) {
				JOptionPane.showMessageDialog(null, "Unknown compiler " + node.getStringValue("compiler"));
				return null;
			}

			String name = node.getStringValue("name");
			List<JsonNode> hotkeyJSONs =  node.getArrayNode("hotkey");
			Set<KeyChain> hotkeys = new HashSet<>();
			JSONUtility.addAllJson(hotkeyJSONs, new Function<JsonNode, KeyChain>(){
				@Override
				public KeyChain apply(JsonNode d) {
					KeyChain value = KeyChain.parseJSON(d.getArrayNode());
					return value;
				}}, hotkeys);

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
			UserDefinedAction output = compiler.compile(source, objectFile).getB();
			if (output == null) {
				JOptionPane.showMessageDialog(null, "Compilation failed for task " + name + " with source at path " + sourcePath);
				return null;
			}

			UsageStatistics statistics = UsageStatistics.parseJSON(node.getNode("statistics"));
			if (statistics != null) {
				output.statistics = statistics;
			} else {
				output.statistics.createNow();
				LOGGER.warning("Unable to retrieve statistics for task " + name);
			}

			boolean enabled = node.getBooleanValue("enabled");



			output.sourcePath = sourcePath;
			output.compiler = compiler.getName();
			output.name = name;
			output.hotkeys = hotkeys;
			output.enabled = enabled;

			return output;
		} catch (Exception e) {
			Logger.getLogger(UserDefinedAction.class.getName()).log(Level.WARNING, "Exception parsing task from JSON", e);
			return null;
		}
	}

	@Override
	public final Logger getLogger() {
		return Logger.getLogger(UserDefinedAction.class.getName());
	}
}