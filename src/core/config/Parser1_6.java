package core.config;

import java.util.logging.Level;
import java.util.logging.Logger;

import utilities.JSONUtility;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;

public class Parser1_6 extends ConfigParser {

	private static final Logger LOGGER = Logger.getLogger(Parser1_6.class.getName());

	@Override
	protected String getVersion() {
		return "1.6";
	}

	@Override
	protected String getPreviousVersion() {
		return "1.5";
	}

	@Override
	protected JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion) {
		try {
			JsonNode globalSettings = JsonNodeFactories.object(
					JsonNodeFactories.field("debug",
							JsonNodeFactories.object(
									JsonNodeFactories.field("level", JsonNodeFactories.string(Level.WARNING.toString()))
							)
					),
					JsonNodeFactories.field("tray_icon_enabled", JsonNodeFactories.booleanNode(true)),
					JsonNodeFactories.field("enabled_halt_by_key", JsonNodeFactories.booleanNode(true))
					);

			JsonNode newNode = JSONUtility.addChild(previousVersion, "global_settings", globalSettings);
			return newNode.getRootNode();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to convert json from previous version " + getPreviousVersion(), e);
			return null;
		}
	}

	@Override
	protected boolean internalExtractData(Config config, JsonRootNode root) {
		try {
			//Convert to 1_7
			Parser1_7 parser = new Parser1_7();
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