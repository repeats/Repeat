package staticResources;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import utilities.FileUtility;
import utilities.Function;

public abstract class AbstractBootstrapResource {

	private static final Logger LOGGER = Logger.getLogger(AbstractBootstrapResource.class.getName());

	protected void extractResources() throws IOException {
		if (!FileUtility.createDirectory(getExtractingDest().getAbsolutePath())) {
			LOGGER.warning("Failed to extract " + getName() + " resources");
			return;
		}

		final String path = getRelativeSourcePath();
		FileUtility.extractFromCurrentJar(path, getExtractingDest(), new Function<String, Boolean>() {
			@Override
			public Boolean apply(String name) {
				return correctExtension(name);
			}
		});
	}

	protected abstract boolean correctExtension(String name);
	protected abstract String getRelativeSourcePath();
	protected abstract File getExtractingDest();
	protected abstract String getName();
}
