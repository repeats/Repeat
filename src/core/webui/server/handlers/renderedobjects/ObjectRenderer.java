package core.webui.server.handlers.renderedobjects;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateNotFoundException;

public class ObjectRenderer {

	private static final Logger LOGGER = Logger.getLogger(ObjectRenderer.class.getName());

	private final String TEMPLATE_EXTENSION = ".ftlh";
	private final Configuration config;

	public ObjectRenderer(File templateDir) {
		config = new Configuration(Configuration.VERSION_2_3_28);
		try {
			config.setDirectoryForTemplateLoading(templateDir);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error setting template directory to " + templateDir.getAbsolutePath(), e);
		}
		config.setDefaultEncoding("UTF-8");
		config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		config.setLogTemplateExceptions(false);
		config.setWrapUncheckedExceptions(true);
	}

	/**
	 * @param templateFile relative to template dir. Extension is automatically appended.
	 */
	public String render(String templateFile, Map<String, Object> data) {
		try {
			return internalRender(templateFile, data);
		} catch (IOException | TemplateException e) {
			LOGGER.log(Level.WARNING, "Encountered error when rendering template.", e);
			return null;
		}
	}

	private String internalRender(String templateFile, Map<String, Object> data) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException {
		if (!templateFile.endsWith(TEMPLATE_EXTENSION)) {
			templateFile += TEMPLATE_EXTENSION;
		}

		Template template = config.getTemplate(templateFile);
        Writer output = new StringWriter();
        template.process(data, output);
        return output.toString();
	}
}
