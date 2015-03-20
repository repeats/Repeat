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
	protected JsonRootNode convertFromPreviousVersion(JsonRootNode previousVersion) {
		return previousVersion;
	}

	@Override
	protected boolean internalExtractData(JsonRootNode root) {
		try {
			Config.RECORD = Integer.parseInt(root.getNode("global_hotkey").getNumberValue("record"));
			Config.REPLAY = Integer.parseInt(root.getNode("global_hotkey").getNumberValue("replay"));
			Config.COMPILED_REPLAY = Integer.parseInt(root.getNode("global_hotkey").getNumberValue("replay_compiled"));
			return true;
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to parse json", e);
			return false;
		}
	}
}