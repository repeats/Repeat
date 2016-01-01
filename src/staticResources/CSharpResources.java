package staticResources;

import java.io.File;
import java.util.logging.Logger;

import utilities.FileUtility;
import core.languageHandler.Language;


public class CSharpResources extends AbstractNativeBootstrapResource {

	@Override
	protected boolean correctExtension(String name) {
		return name.endsWith(".exe") || name.endsWith(".dll");
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(CSharpResources.class.getName());
	}

	@Override
	protected File getExtractingDest() {
		return new File(FileUtility.joinPath("resources", "csharp"));
	}

	@Override
	protected Language getName() {
		return Language.CSHARP;
	}

	@Override
	protected String getRelativeSourcePath() {
		return "natives/csharp/bin";
	}

	@Override
	protected boolean generateKeyCode() {
		return true;
	}

	@Override
	public File getIPCClient() {
		return new File("resources/csharp/Repeat.exe");
	}
}