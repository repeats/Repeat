package core.config;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.userDefinedTask.TaskGroup;
import utilities.json.JSONUtility;

public class Parser2_11 extends ConfigParser {

	@Override
	protected String getVersion() {
		return "2.11";
	}

	@Override
	protected String getPreviousVersion() {
		return "2.10";
	}

	@Override
	protected JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion) {
		JsonNode oldSettings = previousVersion.getNode("global_settings");
		JsonNode newSettings = JSONUtility.addChild(oldSettings, "run_task_with_server_config", JsonNodeFactories.booleanNode(false));
		return JSONUtility.replaceChild(previousVersion, "global_settings", newSettings).getRootNode();
	}

	@Override
	protected boolean internalImportData(Config config, JsonRootNode root) {
		boolean result = true;

		for (JsonNode taskGroupNode : root.getArrayNode("task_groups")) {
			TaskGroup taskGroup = TaskGroup.parseJSON(config.getCompilerFactory(), taskGroupNode, ConfigParsingMode.IMPORT_PARSING);
			result &= taskGroup != null;
			if (taskGroup != null) {
				result &= config.getBackEnd().addPopulatedTaskGroup(taskGroup);
			}
		}
		return result;
	}
}