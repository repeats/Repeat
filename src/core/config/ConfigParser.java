package core.config;

import argo.jdom.JsonRootNode;

abstract class ConfigParser {
	protected abstract String getVersion();
	protected abstract String getPreviousVersion();

	protected abstract JsonRootNode convertFromPreviousVersion(JsonRootNode previousVersion);

	protected final boolean extractData(Config config, JsonRootNode data) {
		try {
			//Sanity check
			if (!data.getStringValue("version").equals(getVersion())) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}

		return internalExtractData(config, data);
	}
	protected abstract boolean internalExtractData(Config config, JsonRootNode data);
}