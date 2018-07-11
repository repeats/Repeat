package core.config;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import utilities.FileUtility;
import utilities.Function;
import utilities.json.JSONUtility;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;

public class Parser1_3 extends ConfigParser {

	private static final Logger LOGGER = Logger.getLogger(Parser1_3.class.getName());

	@Override
	protected String getVersion() {
		return "1.3";
	}

	@Override
	protected String getPreviousVersion() {
		return "1.2";
	}

	@Override
	protected JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion) {
		try {
			final File currentPath = new File("");
			JsonNode taskGroup = JsonNodeFactories.array(new Function<JsonNode, JsonNode>(){
				@Override
				public JsonNode apply(JsonNode taskGroup) {
					return JSONUtility.replaceChild(taskGroup, "tasks", JsonNodeFactories.array(
							new Function<JsonNode, JsonNode>() {
								@Override
								public JsonNode apply(JsonNode task) {
									String sourcePath = task.getStringValue("source_path");
									String relativePath = FileUtility.getRelativePath(currentPath, new File(sourcePath));
									return JSONUtility.replaceChild(task, "source_path", JsonNodeFactories.string(relativePath));
								}
							}.map(taskGroup.getArrayNode("tasks"))));
				}
			}.map(previousVersion.getArrayNode("task_groups")));

			JsonRootNode newRoot = JsonNodeFactories.object(
					JsonNodeFactories.field("version", JsonNodeFactories.string(getVersion())),
					JsonNodeFactories.field("global_hotkey", previousVersion.getNode("global_hotkey")),
					JsonNodeFactories.field("compilers", previousVersion.getNode("compilers")),
					JsonNodeFactories.field("task_groups", taskGroup)
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