package core.config;

import java.util.logging.Level;
import java.util.logging.Logger;

import utilities.Function;
import utilities.JSONUtility;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.languageHandler.Language;

public class Parser1_8 extends ConfigParser {

	private static final Logger LOGGER = Logger.getLogger(Parser1_8.class.getName());

	@Override
	protected String getVersion() {
		return "1.8";
	}

	@Override
	protected String getPreviousVersion() {
		return "1.7";
	}

	@Override
	protected JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion) {
		try {
			JsonNode globalHotkeys = previousVersion.getNode("global_hotkey");
			JsonNode result = JSONUtility.removeChild(previousVersion, "global_hotkey");

			JsonNode globalSettings = JSONUtility.addChild(result.getNode("global_settings"), "global_hotkey", globalHotkeys);
			result = JSONUtility.replaceChild(result, "global_settings", globalSettings);
			result = JSONUtility.addChild(result, "ipc_settings", JsonNodeFactories.array(
					new Function<Language, JsonNode>(){
						@Override
						public JsonNode apply(Language l) {
							return JsonNodeFactories.object(
										JsonNodeFactories.field("name", JsonNodeFactories.string(l.toString())),
										JsonNodeFactories.field("launch_at_startup", JsonNodeFactories.booleanNode(true))
										);
						}
					}.map(Language.ALL_LANGUAGES)
				));

			return result.getRootNode();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to convert json from previous version " + getPreviousVersion(), e);
			return null;
		}
	}

	@Override
	protected boolean internalExtractData(Config config, JsonRootNode root) {
		try {
			//Convert to 1_9
			Parser1_9 parser = new Parser1_9();
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

	@Override
	protected boolean importData(Config config, JsonRootNode root) {
		Parser1_9 parser = new Parser1_9();
		return parser.importData(config, root);
	}
}