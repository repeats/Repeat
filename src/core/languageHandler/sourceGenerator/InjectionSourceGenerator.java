package core.languageHandler.sourceGenerator;

import java.util.logging.Logger;

import staticResources.BootStrapResources;

public abstract class InjectionSourceGenerator extends AbstractSourceGenerator {

	private static final Logger LOGGER = Logger.getLogger(InjectionSourceGenerator.class.getName());

	/**
	 * The template must ensure that {@link #GENERATED_CODE_SECTION_SIGNAL} is present in the code, and
	 * that generated code (either by human or source generator) can be injected on the next line without
	 * breaking compilability of the generated source code.
	 */
	private static final String GENERATED_CODE_SECTION_SIGNAL = "Begin generated code";

	@Override
	public String getSource() {
		String mainSource = sourceScheduler.getSource();
		if (mainSource == null) {
			LOGGER.severe("Unable to generate source...");
			mainSource = "";
		}

		StringBuffer sb = new StringBuffer();
		String template = BootStrapResources.getNativeLanguageTemplate(getSourceLanguage());
		int generatedCodeIndex = template.indexOf(GENERATED_CODE_SECTION_SIGNAL);
		if (generatedCodeIndex == -1) {
			LOGGER.severe("Unable to generate source code. Missing generated code section signal " + GENERATED_CODE_SECTION_SIGNAL);
			return "";
		}

		// Find the next line to inject code
		int injectingIndex = template.indexOf('\n', generatedCodeIndex);
		if (injectingIndex == -1) { // There is no new line after this. We inject code at the end of the template
			injectingIndex = template.length();
		}

		// Split the template and inject source code
		sb.append(template.substring(0, injectingIndex));
		sb.append('\n');
		sb.append(mainSource);
		sb.append(template.substring(injectingIndex + 1, template.length()));

		return sb.toString();
	}
}
