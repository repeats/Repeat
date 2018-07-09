package core.webui.server;

import java.io.File;
import java.util.logging.Logger;

import utilities.FileUtility;

public class ResourceManager {

	private static final Logger LOGGER = Logger.getLogger(ResourceManager.class.getName());

	private String root;

	public ResourceManager(File root) {
		this.root = root.getAbsolutePath();
	}

	public String getResourceContent(String path) {
		String filePath = FileUtility.joinPath(root, path);
		StringBuffer content = FileUtility.readFromFile(filePath);
		if (content == null) {
			LOGGER.warning("Unable to get resource content for path " + path);
			return "";
		}
		return content.toString();
	}
}
