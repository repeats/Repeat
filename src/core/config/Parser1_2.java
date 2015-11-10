package core.config;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import utilities.FileUtility;
import utilities.Function;
import utilities.JSONUtility;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.languageHandler.Languages;

public class Parser1_2 extends ConfigParser {

	private static final Logger LOGGER = Logger.getLogger(Parser1_2.class.getName());

	/**
	 * This is first used with release 1.7.1
	 */
	@Override
	protected String getVersion() {
		return "1.2";
	}

	@Override
	protected String getPreviousVersion() {
		return "1.1";
	}

	@Override
	protected JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion) {
		try {
			JsonNode taskGroup = JsonNodeFactories.array(new Function<JsonNode, JsonNode>(){
				@Override
				public JsonNode apply(JsonNode taskGroup) {
					return JSONUtility.replaceChild(taskGroup, "tasks", JsonNodeFactories.array(
							new Function<JsonNode, JsonNode>() {
								@Override
								public JsonNode apply(JsonNode task) {
									String compiler = task.getStringValue("compiler");
									File f = new File(task.getStringValue("source_path"));
									File newFile = null;
									String newName = f.getName();

									if (compiler.equals(Languages.JAVA.toString())) {
										if (!newName.startsWith("CC_")) {
											newName = "CC_" + newName;
										}

										if (!newName.endsWith(".java")) {
											newName += ".java";
										}
									} else if (compiler.equals(Languages.PYTHON.toString())) {
										if (!newName.startsWith("PY_")) {
											newName = "PY_" + newName;
										}

										if (!newName.endsWith(".py")) {
											newName = newName + ".py";
										}
									}
									newFile = FileUtility.renameFile(f, newName);

									if (FileUtility.fileExists(f)) {
										f.renameTo(newFile);
									}

									return JSONUtility.replaceChild(task, "source_path", JsonNodeFactories.string(newFile.getAbsolutePath()));
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
	protected boolean internalExtractData(Config config, JsonRootNode root) {
		try {
			//Convert to 1_3
			Parser1_3 parser = new Parser1_3();
			JsonRootNode newRoot = parser.convertFromPreviousVersion(root);

			if (newRoot != null) {
				return parser.extractData(config, newRoot);
			} else {
				LOGGER.log(Level.WARNING, "Unable to convert to later version " + parser.getVersion());
				return false;
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to parse json", e);
			return false;
		}
	}
}