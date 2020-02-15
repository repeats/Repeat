package staticResources;

import java.io.File;

import utilities.FileUtility;

public class WebUIResources extends AbstractBootstrapResource {

	@Override
	protected boolean correctExtension(String name) {
		return true;
	}

	@Override
	protected File getExtractingDest() {
		return new File(FileUtility.joinPath("resources", "webui"));
	}

	@Override
	protected String getName() {
		return "WebUI";
	}

	@Override
	protected String getRelativeSourcePath() {
		return "staticContent/webui";
	}

	/**
	 * Returns the directory containing all web UI resources.
	 */
	public File getRoot() {
		return getExtractingDest();
	}

	/**
	 * Returns the directory containing all static resources.
	 */
	public File getStaticDir() {
		return new File(FileUtility.joinPath(getRoot().getAbsolutePath(), "static"));
	}

	/**
	 * Returns the directory containing all HTML templates.
	 */
	public File getTemplateDir() {
		return new File(FileUtility.joinPath(getRoot().getAbsolutePath(), "templates"));
	}
}
