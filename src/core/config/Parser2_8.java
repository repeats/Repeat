package core.config;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.userDefinedTask.TaskGroup;
import utilities.json.JSONUtility;

public class Parser2_8 extends ConfigParser {

	@Override
	protected String getVersion() {
		return "2.8";
	}

	@Override
	protected String getPreviousVersion() {
		return "2.7";
	}

	@Override
	protected JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion) {
		JsonNode globalSettings = previousVersion.getNode("global_settings");
		JsonNode updated = JSONUtility.addChild(globalSettings, "use_clipboard_to_type_string", JsonNodeFactories.booleanNode(false));
		return JSONUtility.replaceChild(previousVersion, "global_settings", updated).getRootNode();
	}

	@Override
	protected boolean internalImportData(Config config, JsonRootNode root) {
		boolean result = true;

		for (JsonNode taskGroupNode : root.getArrayNode("task_groups")) {
			TaskGroup taskGroup = TaskGroup.parseJSON(config.getCompilerFactory(), taskGroupNode);
			result &= taskGroup != null;
			if (taskGroup != null) {
				result &= config.getBackEnd().addPopulatedTaskGroup(taskGroup);
			}
		}
		return result;
	}
}