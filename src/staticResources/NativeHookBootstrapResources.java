package staticResources;

import java.io.File;

import org.simplenativehooks.NativeHookInitializer;

import utilities.FileUtility;
import utilities.OSIdentifier;

public class NativeHookBootstrapResources extends AbstractBootstrapResource {

	public static File getNativeHookDirectory() {
		return new File(FileUtility.joinPath("resources", "nativehooks", getOSDir()));
	}

	public static File getNativeHookExecutable() {
		String file = "";

		if (OSIdentifier.IS_WINDOWS) {
			file = "RepeatHook.exe";
		}
		if (OSIdentifier.IS_LINUX) {
			file = "RepeatHook.out";
		}
		if (OSIdentifier.IS_OSX) {
			file = "RepeatHook.out";
		}
		return new File(FileUtility.joinPath(getNativeHookDirectory().getAbsolutePath(), file));
	}

	@Override
	protected boolean postProcessing(String name) {
		if (OSIdentifier.IS_LINUX) {
			if (NativeHookInitializer.USE_X11_ON_LINUX) {
				if (name.endsWith("RepeatHookX11Key.out") || name.endsWith("RepeatHookX11Mouse.out")) {
					return new File(name).setExecutable(true);
				}
			} else if (name.endsWith("RepeatHook.out")) {
				return new File(name).setExecutable(true);
			}
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
			if (NativeHookInitializer.USE_X11_ON_LINUX) {
				return name.endsWith("RepeatHookX11Key.out") || name.endsWith("RepeatHookX11Mouse.out");
			} else {
				return name.endsWith("RepeatHook.out");
			}
		}
		if (OSIdentifier.IS_OSX) {
			return name.endsWith("RepeatHook.out");
		}
		throw new IllegalStateException("OS is unsupported.");
	}

	@Override
	protected String getRelativeSourcePath() {
		return "org/simplenativehooks/" + getOSDir() + "/nativecontent";
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
			if (NativeHookInitializer.USE_X11_ON_LINUX) {
				return "x11";
			} else {
				return "linux";
			}
		} else if (OSIdentifier.IS_OSX) {
			return "osx";
		}
		throw new IllegalStateException("OS is unsupported.");
	}
}
