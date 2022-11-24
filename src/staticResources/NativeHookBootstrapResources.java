package staticResources;

import java.io.IOException;
import java.net.URISyntaxException;

import org.simplenativehooks.staticResources.BootStrapResources;

public class NativeHookBootstrapResources implements BootstrapResourcesExtrator {

	@Override
	public void extractResources() throws IOException, URISyntaxException {
		BootStrapResources.extractResources();
	}
}
