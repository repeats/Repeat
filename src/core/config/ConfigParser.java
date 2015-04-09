package core.config;

import argo.jdom.JsonRootNode;

import com.sun.istack.internal.logging.Logger;

abstract class ConfigParser {
	protected abstract String getVersion();
	protected abstract String getPreviousVersion();

	protected final JsonRootNode convertFromPreviousVersion(JsonRootNode previousVersion) {
		if (previousVersion != null && previousVersion.isStringValue("version")) {
			if (previousVersion.getStringValue("version").equals(getPreviousVersion())) {
				return internalConvertFromPreviousVersion(previousVersion);
			}
		}
		Logger.getLogger(ConfigParser.class).warning("Invalid previous version " + getPreviousVersion() + " cannot "
				+ "be converted to this version " + getVersion() + ". Only accept previous version " + getPreviousVersion());
		return null;
	}

	protected abstract JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion);

	protected final boolean extractData(Config config, JsonRootNode data) {
		try {
			//Sanity check
			if (!data.getStringValue("version").equals(getVersion())) {
				Logger.getLogger(ConfigParser.class).warning("Invalid version " + data.getStringValue("version") + " with parser of version "
						+ getVersion());
				return false;
			}
		} catch (Exception e) {
			Logger.getLogger(ConfigParser.class).warning("Cannot parse json", e);
			return false;
		}

		return internalExtractData(config, data);
	}
	protected abstract boolean internalExtractData(Config config, JsonRootNode data);
}