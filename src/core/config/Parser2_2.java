package core.config;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.languageHandler.Language;
import utilities.json.JSONUtility;

public class Parser2_2 extends ConfigParser {

	private static final Logger LOGGER = Logger.getLogger(Parser2_2.class.getName());

	@Override
	protected String getVersion() {
		return "2.2";
	}

	@Override
	protected String getPreviousVersion() {
		return "2.1";
	}

	@Override
	protected JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion) {
		try {
			// Add classpath to java compiler as compiler specific args
			List<JsonNode> compilers = previousVersion.getArrayNode("compilers");

			List<JsonNode> replacement = new ArrayList<>();
			for (JsonNode compiler : compilers) {
				if (compiler.getStringValue("name").equals(Language.JAVA.toString())) {
					replacement.add(JSONUtility.replaceChild(compiler, "compiler_specific_args",
							JsonNodeFactories.object(JsonNodeFactories.field("classpath", JsonNodeFactories.array()))));
				} else {
					replacement.add(compiler);
				}
			}

			return JSONUtility.replaceChild(previousVersion, "compilers", JsonNodeFactories.array(replacement)).getRootNode();
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