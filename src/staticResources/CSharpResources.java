package staticResources;

import java.io.File;

import core.languageHandler.Language;
import utilities.FileUtility;


public class CSharpResources extends AbstractNativeBootstrapResource {

	@Override
	protected boolean correctExtension(String name) {
		return name.endsWith(".exe") || name.endsWith(".dll");
	}

	@Override
	protected File getExtractingDest() {
		return new File(FileUtility.joinPath("resources", "csharp"));
	}

	@Override
	protected Language getLanguage() {
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