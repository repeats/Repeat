package core.config;

import java.io.File;

import javax.swing.JOptionPane;

import argo.jdom.JsonRootNode;
import utilities.FileUtility;
import utilities.JSONUtility;

public class CliConfig {
	public static final int DEFAULT_SERVER_PORT = 65432;

	private int serverPort;

	public CliConfig() {
		this.serverPort = DEFAULT_SERVER_PORT;
	}

	public void loadConfig(File file) {
		File configFile = file == null ? new File(Config.CONFIG_FILE_NAME) : file;
		if (FileUtility.fileExists(configFile)) {
			JsonRootNode root = JSONUtility.readJSON(configFile);

			if (root == null) {
				JOptionPane.showMessageDialog(null, "Config file is not in json format");
				return;
			} else if (!root.isStringValue("version")) {
				JOptionPane.showMessageDialog(null, "Config file is in unknown version");
				return;
			}

			String version = root.getStringValue("version");
			ConfigParser parser = Config.getConfigParser(version);
			boolean foundVersion = parser != null;
			boolean extractResult = false;
			if (foundVersion) {
				extractResult = parser.extractData(this, root);
			}

			if (!foundVersion) {
				JOptionPane.showMessageDialog(null, "Config file is in unknown version " + version);
				defaultExtract();
			}

			if (!extractResult) {
				JOptionPane.showMessageDialog(null, "Cannot extract result with version " + version);
				defaultExtract();
			}
		} else {
			defaultExtract();
		}
	}

	private void defaultExtract() {
		// Nothing to do here.
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
}
