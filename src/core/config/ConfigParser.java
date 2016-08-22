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
			// Sanity check
			if (!data.getStringValue("version").equals(getVersion())) {
				LOGGER.warning("Invalid version " + data.getStringValue("version") + " with parser of version "
						+ getVersion());
				return false;
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Cannot parse json", e);
			return false;
		}

		if (!Config.CURRENT_CONFIG_VERSION.equals(getVersion())) { // Then convert to latest version then parse
			LOGGER.info("Looking for next version " + getVersion());
			String currentVersion = getVersion();
			while (!currentVersion.equals(Config.CURRENT_CONFIG_VERSION)) {
				ConfigParser nextVersion = Config.getNextConfigParser(currentVersion);

				if (nextVersion == null) {
					LOGGER.warning("Unable to find the next version of current version " + currentVersion);
					return false;
				}

				try {
					data = nextVersion.convertFromPreviousVersion(data);
				} catch (Exception e) {
					LOGGER.log(Level.WARNING, "Unable to convert from version " + currentVersion, e);
					data = null;
				}

				if (data == null) {
					LOGGER.log(Level.WARNING, "Unable to convert to later version " + nextVersion.getVersion());
					return false;
				}
				currentVersion = nextVersion.getVersion();
			}

			ConfigParser parser = Config.getConfigParser(currentVersion);
			return parser.extractData(config, data);
		} else {
			return internalExtractData(config, data);
		}
	}

	protected boolean internalExtractData(Config config, JsonRootNode data) {
		throw new UnsupportedOperationException("Config version " + getVersion() + " does not support extracting data. "
				+ "Convert to a later version.");
	}

	protected final boolean importData(Config config, JsonRootNode data) {
		return internalImportData(config, data);
	}

	protected abstract boolean internalImportData(Config config, JsonRootNode data);
}