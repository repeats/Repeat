package core.config;

import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;
import core.userDefinedTask.TaskGroup;

public class Parser2_7 extends ConfigParser {

	@Override
	protected String getVersion() {
		return "2.7";
	}

	@Override
	protected String getPreviousVersion() {
		return "2.6";
	}

	@Override
	protected JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion) {
		return previousVersion; // Nothing to convert.
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