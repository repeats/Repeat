package core.config;

import java.util.logging.Level;
import java.util.logging.Logger;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import utilities.JSONUtility;

public class Parser1_9 extends ConfigParser {

	private static final Logger LOGGER = Logger.getLogger(Parser1_9.class.getName());

	@Override
	protected String getVersion() {
		return "1.9";
	}

	@Override
	protected String getPreviousVersion() {
		return "1.8";
	}

	@Override
	protected JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion) {
		try {
			JsonNode globalSettings = previousVersion.getNode("global_settings");
			globalSettings = JSONUtility.addChild(globalSettings,
													"execute_on_key_released",
													JsonNodeFactories.booleanNode(false));
			return JSONUtility.replaceChild(previousVersion, "global_settings", globalSettings).getRootNode();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to convert json from previous version " + getPreviousVersion(), e);
			return null;
		}
	}

	@Override
	protected boolean internalImportData(Config config, JsonRootNode root) {
		ConfigParser parser = Config.getNextConfigParser(getVersion());
		return parser.internalImportData(config, root);
	}
}