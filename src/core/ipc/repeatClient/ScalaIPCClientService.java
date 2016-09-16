package core.ipc.repeatClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import staticResources.BootStrapResources;
import utilities.FileUtility;
import utilities.JSONUtility;
import utilities.StringUtilities;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import core.languageHandler.Language;

public class ScalaIPCClientService extends IPCClientService {

	private static final Logger LOGGER = Logger.getLogger(ScalaIPCClientService.class.getName());

	private static final String SCALA_MAIN_CLASS = "main.Main";
	private static final String[] SCALA_JARS = new String[]{
		"scala-library.jar",
		"scala-compiler.jar",
		"scala-reflect.jar"
	};

	// The directory where the scala jar files are
	private File scalaLibraryDirectory;

	public void setScalaLibraryDirectory(File directory) {
		this.scalaLibraryDirectory = directory;
	}

	@Override
	public String getName() {
		return "Scala IPC Client";
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(getClass().getName());
	}

	/**
	 * Check for existence of scala libraries (i.e. the jar files).
	 *
	 * @return if all dependency libraries exist.
	 */
	private boolean checkDependencies() {
		if (scalaLibraryDirectory == null || !scalaLibraryDirectory.isDirectory()) {
			LOGGER.warning("Unable to identify scala library directory " + scalaLibraryDirectory.getAbsolutePath());
			return false;
		}

		for (String libraryName : SCALA_JARS) {
			String library = FileUtility.joinPath(scalaLibraryDirectory.getAbsolutePath(), libraryName);
			if (!new File(library).canRead()) {
				LOGGER.warning("Missing or inaccessible library " + library);
				return false;
			}
		}

		return true;
	}

	/**
	 * Get the classpath required to launch scala jar. This includes the jar itself and the dependency scala jar libraries.
	 * This assumes that {@link #scalaLibraryDirectory} is already set to the correct value.
	 *
	 * @return the required classpath to launch scala jar.
	 */
	private String getClassPath() {
		String scalaJarPath = BootStrapResources.getBootstrapResource(Language.SCALA).getIPCClient().getAbsolutePath();

		List<String> paths = new ArrayList<>();
		for (String library : SCALA_JARS) {
			paths.add(FileUtility.joinPath(scalaLibraryDirectory.getAbsolutePath(), library));
		}
		paths.add(scalaJarPath);
		paths.add(".");

		return StringUtilities.join(paths, File.pathSeparator);
	}

	@Override
	protected String[] getLaunchCmd() {
		if (!checkDependencies()) {
			return null;
		}

		return new String[] { executingProgram.getAbsolutePath(), "-classpath" , "\"" + getClassPath() + "\"", SCALA_MAIN_CLASS};
	}

	@Override
	protected JsonNode getSpecificConfig() {
		JsonNode config = super.getSpecificConfig();
		return JSONUtility.addChild(config, "scalaLibraryDirectory",
				scalaLibraryDirectory == null ?
						JsonNodeFactories.nullNode() : JsonNodeFactories.string(scalaLibraryDirectory.getAbsolutePath()));
	}

	@Override
	protected boolean extractSpecificConfig(JsonNode node) {
		if (!super.extractSpecificConfig(getSpecificConfig())) {
			return false;
		}

		if (node.isNode("scalaLibraryDirectory") && !node.isNullNode("scalaLibraryDirectory")) {
			scalaLibraryDirectory = new File(node.getStringValue("scalaLibraryDirectory"));
		} else {
			scalaLibraryDirectory = null;
		}

		return true;
	}
}
