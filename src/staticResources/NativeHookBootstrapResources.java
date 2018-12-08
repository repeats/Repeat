package staticResources;

import java.io.File;

import utilities.FileUtility;
import utilities.OSIdentifier;

public class NativeHookBootstrapResources extends AbstractBootstrapResource {

	public static File getNativeHookExecutable() {
		String[] paths = new String[] { "resources", "nativehooks", getOSDir(), "" };
		if (OSIdentifier.IS_WINDOWS) {
			paths[3] = "RepeatHook.exe";
		}
		if (OSIdentifier.IS_LINUX) {
			paths[3] = "RepeatHook.out";
		}
		if (OSIdentifier.IS_OSX) {
			paths[3] = "RepeatHook.out";
		}
		return new File(FileUtility.joinPath(paths));
	}

	@Override
	protected boolean postProcessing(String name) {
		if (OSIdentifier.IS_LINUX && name.endsWith("RepeatHook.out")) {
			return new File(name).setExecutable(true);
		}
		if (OSIdentifier.IS_OSX && name.endsWith("RepeatHook.out")) {
			return new File(name).setExecutable(true);
		}
		return true;
	}

	@Override
	protected boolean correctExtension(String name) {
		if (OSIdentifier.IS_WINDOWS) {
			return name.endsWith("RepeatHook.exe");
		}
		if (OSIdentifier.IS_LINUX) {
			return name.endsWith("RepeatHook.out");
		}
		if (OSIdentifier.IS_OSX) {
			return name.endsWith("RepeatHook.out");
		}
		throw new IllegalStateException("OS is unsupported.");
	}

	@Override
	protected String getRelativeSourcePath() {
		return "nativehooks/" + getOSDir() + "/nativecontent";
	}

	@Override
	protected File getExtractingDest() {
		return new File(FileUtility.joinPath("resources", "nativehooks", getOSDir()));
	}

	@Override
	protected String getName() {
		return "NativeHook";
	}

	private static String getOSDir() {
		if (OSIdentifier.IS_WINDOWS) {
			return "windows";
		} else if (OSIdentifier.IS_LINUX) {
			return "linux";
		} else if (OSIdentifier.IS_OSX) {
			return "osx";
		}
		throw new IllegalStateException("OS is unsupported.");
	}
}
