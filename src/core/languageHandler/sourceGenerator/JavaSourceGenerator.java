package core.languageHandler.sourceGenerator;

import java.util.logging.Logger;

import staticResources.BootStrapResources;
import utilities.Function;
import core.languageHandler.Language;
import core.scheduler.SchedulingData;

public class JavaSourceGenerator extends AbstractSourceGenerator {

	private static final Logger LOGGER = Logger.getLogger(JavaSourceGenerator.class.getName());

	/**
	 * The template must ensure that {@link #GENERATED_CODE_SECTION_SIGNAL} is present in the code, and
	 * that generated code (either by human or source generator) can be injected on the next line without
	 * breaking compilability of the generated source code.
	 */
	private static final String GENERATED_CODE_SECTION_SIGNAL = "Begin generated code";

	public JavaSourceGenerator() {
		super();
		this.sourceScheduler.setSleepSource(new Function<Long, String>() {
			@Override
			public String apply(Long r) {
				return TWO_TAB + "controller.blockingWait(" + r + ");\n";
			}
		});
	}

	@Override
	public boolean internalSubmitTask(long time, String device, String action, int[] param) {
		String mid = "";
		if (device.equals("mouse")) {
			if (action.equals("move")) {
				mid = "controller.mouse().move(" + param[0] + ", " + param[1] +");";
			} else if (action.equals("moveBy")) {
				mid = "controller.mouse().moveBy(" + param[0] + ", " + param[1] +");";
			} else if (action.equals("click")) {
				mid = "controller.mouse().click(" + param[0] + ");";
			} else if (action.equals("press")) {
				mid = "controller.mouse().press(" + param[0] + ");";
			} else if (action.equals("release")) {
				mid = "controller.mouse().release(" + param[0] + ");";
			} else {
				return false;
			}
		} else if (device.equals("keyBoard")) {
			if (action.equals("type")) {
				mid = "controller.keyBoard().type(" + param[0] + ");";
			} else if (action.equals("press")) {
				mid = "controller.keyBoard().press(" + param[0] + ");";
			} else if (action.equals("release")) {
				mid = "controller.keyBoard().release(" + param[0] + ");";
			} else {
				return false;
			}
		} else if (action.equals("wait")) {
			mid = "controller.blockingWait(" + param[0] + ");";
		}

		return sourceScheduler.addTask(new SchedulingData<String>(time, TWO_TAB + mid + "\n"));
	}

	@Override
	public String getSource() {
		String mainSource = sourceScheduler.getSource();
		if (mainSource == null) {
			LOGGER.severe("Unable to generate source...");
			mainSource = "";
		}

		StringBuffer sb = new StringBuffer();
		String template = BootStrapResources.getNativeLanguageTemplate(Language.JAVA);
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
