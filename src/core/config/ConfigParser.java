package core.config;

import argo.jdom.JsonRootNode;

abstract class ConfigParser {
	protected abstract String getVersion();
	protected abstract String getPreviousVersion();

	protected abstract JsonRootNode convertFromPreviousVersion(JsonRootNode previousVersion);
	protected abstract void extractData();
}
