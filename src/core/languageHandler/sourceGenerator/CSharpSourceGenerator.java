package core.languageHandler.sourceGenerator;

import java.util.logging.Logger;

import staticResources.BootStrapResources;
import utilities.Function;
import core.languageHandler.Language;
import core.scheduler.SchedulingData;

public class CSharpSourceGenerator extends AbstractSourceGenerator {

	private static final Logger LOGGER = Logger.getLogger(CSharpSourceGenerator.class.getName());

	/**
	 * The template must ensure that {@link #GENERATED_CODE_SECTION_SIGNAL} is present in the code, and
	 * that generated code (either by human or source generator) can be injected on the next line without
	 * breaking compilability of the generated source code.
	 */
	private static final String GENERATED_CODE_SECTION_SIGNAL = "Begin generated code";

	public CSharpSourceGenerator() {
		super();
		this.sourceScheduler.setSleepSource(new Function<Long, String>() {
			@Override
			public String apply(Long r) {
				return FOUR_TAB + "Thread.Sleep(" + r + ");\n";
			}
		});
	}

	@Override
	public boolean internalSubmitTask(long time, String device, String action, int[] param) {
		String mid = "";
		if (device.equals("mouse")) {
			if (action.equals("move")) {
				mid = "mouse.Move(" + param[0] + ", " + param[1] +");\n";
			} else if (action.equals("moveBy")) {
				mid = "mouse.MoveBy(" + param[0] + ", " + param[1] +");\n";
			} else if (action.equals("click")) {
				mid = "mouse.Click(" + param[0] + ");\n";
			} else if (action.equals("press")) {
				mid = "mouse.Press(" + param[0] + ");\n";
			} else if (action.equals("release")) {
				mid = "mouse.Release(" + param[0] + ");\n";
			} else {
				return false;
			}
		} else if (device.equals("keyBoard")) {
			if (action.equals("type")) {
				mid = "key.DoType(" + param[0] + ");\n";
			} else if (action.equals("press")) {
				mid = "key.Press(" + param[0] + ");\n";
			} else if (action.equals("release")) {
				mid = "key.Release(" + param[0] + ");\n";
			} else {
				return false;
			}
		} else if (action.equals("wait")) {
			mid = "Thread.sleep(" + param[0] + ");\n";
		}

		return sourceScheduler.addTask(new SchedulingData<String>(time, FOUR_TAB + mid));
	}

	@Override
	public String getSource() {
		String mainSource = sourceScheduler.getSource();
		if (mainSource == null) {
			LOGGER.severe("Unable to generate source...");
			mainSource = "";
		}

		StringBuffer sb = new StringBuffer();
		String template = BootStrapResources.getNativeLanguageTemplate(Language.CSHARP);
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
