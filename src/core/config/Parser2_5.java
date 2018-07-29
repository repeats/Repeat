package core.config;

import argo.jdom.JsonRootNode;

public class Parser2_5 extends ConfigParser {

	@Override
	protected String getVersion() {
		return "2.5";
	}

	@Override
	protected String getPreviousVersion() {
		return "2.4";
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