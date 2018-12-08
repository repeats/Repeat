package staticResources;

import java.io.File;

import utilities.FileUtility;
import utilities.OSIdentifier;

public class NativeHookResources extends AbstractBootstrapResource {

	@Override
	protected boolean correctExtension(String name) {
		if (OSIdentifier.IS_WINDOWS) {
			return name.equals("RepeatHook.exe");
		}
		if (OSIdentifier.IS_LINUX) {
			return name.equals("RepeatHook.out");
		}
		if (OSIdentifier.IS_OSX) {
			return name.equals("RepeatHook.out");
		}
		return false;
	}

	@Override
	protected String getRelativeSourcePath() {
		return "nativehooks/" + getOSDir() + "/nativecontent";
	}

	@Override
	protected File getExtractingDest() {
		return new File(FileUtility.joinPath("resources", "nativehook", getOSDir()));
	}

	@Override
	protected String getName() {
		return "NativeHook";
	}

	private String getOSDir() {
		String dir = "unknownOS";
		if (OSIdentifier.IS_WINDOWS) {
			return "windows";
		} else if (OSIdentifier.IS_LINUX) {
			return "linux";
		} else if (OSIdentifier.IS_OSX) {
			return "osx";
		}
		return dir;
	}
}
