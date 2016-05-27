package core.config;

import java.util.logging.Level;
import java.util.logging.Logger;

import utilities.Function;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;

public class Parser1_5 extends ConfigParser {

	private static final Logger LOGGER = Logger.getLogger(Parser1_5.class.getName());

	@Override
	protected String getVersion() {
		return "1.5";
	}

	@Override
	protected String getPreviousVersion() {
		return "1.4";
	}

	@Override
	protected JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion) {
		try {
			JsonNode newCompiler = JsonNodeFactories.array(new Function<JsonNode, JsonNode>(){
				@Override
				public JsonNode apply(JsonNode compiler) {
					return JsonNodeFactories.object(
								JsonNodeFactories.field("name", compiler.getNode("name")),
								JsonNodeFactories.field("path", compiler.getNode("path")),
								JsonNodeFactories.field("compiler_specific_args", JsonNodeFactories.object())
								);
				}
			}.map(previousVersion.getArrayNode("compilers")));

			JsonRootNode newRoot = JsonNodeFactories.object(
					JsonNodeFactories.field("version", JsonNodeFactories.string(getVersion())),
					JsonNodeFactories.field("global_hotkey", previousVersion.getNode("global_hotkey")),
					JsonNodeFactories.field("compilers", newCompiler),
					JsonNodeFactories.field("task_groups", previousVersion.getNode("task_groups"))
					);
			return newRoot;
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to convert json from previous version " + getPreviousVersion(), e);
			return null;
		}
	}

	@Override
	protected boolean internalImportData(Config config, JsonRootNode data) {
		LOGGER.warning("Unsupported import data at version " + getVersion());
		return false;
	}
}