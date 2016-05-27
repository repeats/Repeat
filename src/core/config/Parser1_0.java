package core.config;

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
	protected boolean internalImportData(Config config, JsonRootNode data) {
		LOGGER.warning("Unsupported import data at version " + getVersion());
		return false;
	}
}