package core.config;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;
import core.UserDefinedAction;
import core.languageHandler.compiler.DynamicCompiler;

public class Parser1_0 extends ConfigParser {

	private static final Logger LOGGER = Logger.getLogger(Parser1_0.class.getName());

	@Override
	protected String getVersion() {
		return "1.0";
	}

	@Override
	protected String getPreviousVersion() {
		return null;
	}

	@Override
	protected JsonRootNode convertFromPreviousVersion(JsonRootNode previousVersion) {
		return previousVersion;
	}

	@Override
	protected boolean internalExtractData(Config config, JsonRootNode root) {
		try {
			config.RECORD = Integer.parseInt(root.getNode("global_hotkey").getNumberValue("record"));
			config.REPLAY = Integer.parseInt(root.getNode("global_hotkey").getNumberValue("replay"));
			config.COMPILED_REPLAY = Integer.parseInt(root.getNode("global_hotkey").getNumberValue("replay_compiled"));

			for (JsonNode compilerNode : root.getArrayNode("compilers")) {
				String name = compilerNode.getStringValue("name");
				String path = compilerNode.getStringValue("path");
				String runArgs = compilerNode.getStringValue("run_args");

				DynamicCompiler compiler = config.compilerFactory().getCompiler(name);
				if (compiler != null) {
					compiler.setPath(new File(path));
					compiler.setRunArgs(runArgs);
				} else {
					throw new IllegalStateException("Unknown compiler " + name);
				}
			}

			List<UserDefinedAction> tasks = config.getBackEnd().getCustomTasks();
			tasks.clear();
			for (JsonNode taskNode : root.getArrayNode("tasks")) {
				UserDefinedAction task = UserDefinedAction.parseJSON(config.compilerFactory(), taskNode);
				if (task != null) {
					tasks.add(task);
				}
			}

			return true;
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to parse json", e);
			return false;
		}
	}
}