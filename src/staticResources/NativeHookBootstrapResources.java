package staticResources;

import java.io.IOException;

import org.simplenativehooks.staticResources.BootStrapResources;

public class NativeHookBootstrapResources implements BootstrapResourcesExtrator {

	@Override
	public void extractResources() throws IOException {
		BootStrapResources.extractResources();
	}
}
