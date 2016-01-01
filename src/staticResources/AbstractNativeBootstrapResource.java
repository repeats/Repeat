package staticResources;

import java.io.File;
import java.io.IOException;

import utilities.FileUtility;
import utilities.Function;
import utilities.ILoggable;
import core.languageHandler.Language;

public abstract class AbstractNativeBootstrapResource implements ILoggable {
	protected final void extractResources() throws IOException {
		if (!FileUtility.createDirectory(getExtractingDest().getAbsolutePath())) {
			getLogger().warning("Failed to extract " + getName() + " resources");
			return;
		}

		final String path = getRelativeSourcePath();
		FileUtility.extractFromCurrentJar(path, getExtractingDest(), new Function<String, Boolean>() {
			@Override
			public Boolean apply(String name) {
				return correctExtension(name);
			}
		});

		if (!generateKeyCode()) {
			getLogger().warning("Unable to generate key code");
		}
	}

	protected boolean correctExtension(String name) {
		return name.endsWith(".exe") || name.endsWith(".dll");
	}

	protected abstract String getRelativeSourcePath();
	protected abstract File getExtractingDest();
	protected abstract Language getName();
	protected abstract boolean generateKeyCode();
	public abstract File getIPCClient();
}
