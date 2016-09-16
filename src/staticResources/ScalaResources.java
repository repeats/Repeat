package staticResources;

import java.io.File;
import java.util.logging.Logger;

import utilities.FileUtility;
import core.languageHandler.Language;

public class ScalaResources extends AbstractNativeBootstrapResource {

	@Override
	protected boolean correctExtension(String name) {
		return name.endsWith(".jar");
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(ScalaResources.class.getName());
	}

	@Override
	protected String getRelativeSourcePath() {
		return "natives/scala";
	}

	@Override
	protected File getExtractingDest() {
		return new File(FileUtility.joinPath("resources", "scala"));
	}

	@Override
	protected Language getName() {
		return Language.SCALA;
	}

	@Override
	protected boolean generateKeyCode() {
		return true; // No need
	}

	@Override
	public File getIPCClient() {
		return new File("resources/scala/RepeatScala.jar");
	}

}
