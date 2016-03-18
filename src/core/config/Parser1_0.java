package core.config;

import java.util.logging.Level;
import java.util.logging.Logger;

import argo.jdom.JsonRootNode;

public class Parser1_0 extends ConfigParser {

	private static final Logger LOGGER = Logger.getLogger(Parser1_0.class.getName());

	@Override
	protected String getVersion() {
		return "1.0";
	}

	@Override
	protected String getPreviousVersion() {
		return null;
	}

	@Override
	protected JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion) {
		return previousVersion;
	}

	@Override
	protected boolean internalExtractData(Config config, JsonRootNode root) {
		try {
			//Convert to 1_1
			Parser1_1 parser = new Parser1_1();
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
	protected boolean importData(Config config, JsonRootNode data) {
		LOGGER.warning("Unsupported import data at version " + getVersion());
		return false;
	}
}