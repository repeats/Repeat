package core.config;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import utilities.Function;
import utilities.JSONUtility;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.userDefinedTask.TaskGroup;

public class Parser2_1 extends ConfigParser {

	private static final Logger LOGGER = Logger.getLogger(Parser2_1.class.getName());

	@Override
	protected String getVersion() {
		return "2.1";
	}

	@Override
	protected String getPreviousVersion() {
		return "2.0";
	}

	@Override
	protected JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion) {
		try {
			// Move ipc settings into config
			List<JsonNode> ipcSettingNodes = previousVersion.getArrayNode("ipc_settings");
			List<JsonNode> converted = new Function<JsonNode, JsonNode>() {
				@Override
				public JsonNode apply(JsonNode d) {
					String name = d.getStringValue("name");
					boolean launchAtStartup = d.getBooleanValue("launch_at_startup");

					return JsonNodeFactories.object(
							JsonNodeFactories.field("name", JsonNodeFactories.string(name)),
							JsonNodeFactories.field("config", JsonNodeFactories.object(
									JsonNodeFactories.field("launch_at_startup", JsonNodeFactories.booleanNode(launchAtStartup))
									))
							);
				}
			}.map(ipcSettingNodes);

			return JSONUtility.replaceChild(previousVersion, "ipc_settings", JsonNodeFactories.array(converted)).getRootNode();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to convert json from previous version " + getPreviousVersion(), e);
			return null;
		}
	}

	@Override
	protected boolean internalImportData(Config config, JsonRootNode root) {
		boolean result = true;

		List<TaskGroup> taskGroups = config.getBackEnd().getTaskGroups();
		for (JsonNode taskGroupNode : root.getArrayNode("task_groups")) {
			TaskGroup taskGroup = TaskGroup.parseJSON(config.getCompilerFactory(), taskGroupNode);
			result &= taskGroup != null;
			if (taskGroup != null) {
				taskGroups.add(taskGroup);
			}
		}
		return result;
	}
}