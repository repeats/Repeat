package staticResources;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import core.languageHandler.Language;

public abstract class AbstractNativeLanguageBootstrapResource extends AbstractBootstrapResource {

	private static final Logger LOGGER = Logger.getLogger(AbstractNativeLanguageBootstrapResource.class.getName());

	@Override
	protected final void extractResources() throws IOException {
		super.extractResources();
		if (!generateKeyCode()) {
			LOGGER.warning("Unable to generate key code");
		}
	}

	@Override
	protected final String getName() {
		return getLanguage().name();
	}

	protected abstract Language getLanguage();
	protected abstract boolean generateKeyCode();
	public abstract File getIPCClient();
}
