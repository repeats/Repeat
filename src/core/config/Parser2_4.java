package core.config;

import argo.jdom.JsonRootNode;

public class Parser2_4 extends ConfigParser {

	@Override
	protected String getVersion() {
		return "2.4";
	}

	@Override
	protected String getPreviousVersion() {
		return "2.3";
	}

	@Override
	protected JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion) {
		return previousVersion; // Nothing to convert.
	}

	@Override
	protected boolean internalImportData(Config config, JsonRootNode root) {
		ConfigParser parser = Config.getNextConfigParser(getVersion());
		return parser.internalImportData(config, root);
	}
}