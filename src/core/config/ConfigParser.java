package core.config;

import java.util.logging.Level;
import java.util.logging.Logger;

import utilities.JSONUtility;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;

abstract class ConfigParser {

	private static final Logger LOGGER = Logger.getLogger(ConfigParser.class.getName());

	protected abstract String getVersion();
	protected abstract String getPreviousVersion();

	protected final JsonRootNode convertFromPreviousVersion(JsonRootNode previousVersion) {
		if (previousVersion != null && previousVersion.isStringValue("version")) {
			if (previousVersion.getStringValue("version").equals(getPreviousVersion())) {
				JsonRootNode output = internalConvertFromPreviousVersion(previousVersion);

				try {
					JsonNode convertedVersion = JSONUtility.replaceChild(output, "version", JsonNodeFactories.string(getVersion()));
					return convertedVersion.getRootNode();
				} catch (Exception e) {
					LOGGER.log(Level.WARNING, "Unable to modify version when converting versions " + getPreviousVersion() + " to " + getVersion(), e);
					return null;
				}
			}
		}
		LOGGER.warning("Invalid previous version " + getPreviousVersion() + " cannot "
				+ "be converted to this version " + getVersion() + ". Only accept previous version " + getPreviousVersion());
		return null;
	}

	protected abstract JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion);

	protected final boolean extractData(Config config, JsonRootNode data) {
		try {
			//Sanity check
			if (!data.getStringValue("version").equals(getVersion())) {
				LOGGER.warning("Invalid version " + data.getStringValue("version") + " with parser of version "
						+ getVersion());
				return false;
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Cannot parse json", e);
			return false;
		}

		return internalExtractData(config, data);
	}
	protected abstract boolean internalExtractData(Config config, JsonRootNode data);
}