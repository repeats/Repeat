package staticResources;

import java.io.IOException;
import java.net.URISyntaxException;

public interface BootstrapResourcesExtrator {
	public void extractResources() throws IOException, URISyntaxException;
}
