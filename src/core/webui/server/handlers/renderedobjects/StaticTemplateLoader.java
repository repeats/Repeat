package core.webui.server.handlers.renderedobjects;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import freemarker.cache.TemplateLoader;
import staticResources.BootStrapResources;
import staticResources.WebUIResources;

public class StaticTemplateLoader implements TemplateLoader {

	private static final StaticTemplateLoader INSTANCE = new StaticTemplateLoader();

	private StaticTemplateLoader() {}

	public static StaticTemplateLoader of() {
		return INSTANCE;
	}

	@Override
	public void closeTemplateSource(Object arg0) throws IOException {

	}

	@Override
	public Object findTemplateSource(String path) throws IOException {
		// Whatever returned here will be used to pass into other methods.
		// According to documentation at
		// https://freemarker.apache.org/docs/api/freemarker/cache/TemplateLoader.html#findTemplateSource-java.lang.String,
		// this object must implement hashCode and equals.
		return path;
	}

	@Override
	public long getLastModified(Object path) {
		return 0; // Never modified.
	}

	@Override
	public Reader getReader(Object path, String locale) throws IOException {
		InputStream content = BootStrapResources.getStaticContentStream(WebUIResources.TEMPLATES_RESOURCES_PREFIX + path);
		return new InputStreamReader(content);
	}

}
