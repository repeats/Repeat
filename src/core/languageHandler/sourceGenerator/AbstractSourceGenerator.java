package core.languageHandler.sourceGenerator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import staticResources.BootStrapResources;
import core.languageHandler.Language;
import core.scheduler.SchedulingData;

public abstract class AbstractSourceGenerator {

	private static final Logger LOGGER = Logger.getLogger(AbstractSourceGenerator.class.getName());

	protected final StringBuffer source;
	protected TaskSourceScheduler sourceScheduler;

	protected static final String TAB = "    ";
	protected static final String TWO_TAB = TAB + TAB;
	protected static final String THREE_TAB = TWO_TAB + TAB;
	protected static final String FOUR_TAB = THREE_TAB + TAB;

	private static final Map<Language, AbstractSourceGenerator> REFERENCE_SOURCES;
	static {
		REFERENCE_SOURCES = new HashMap<>();
		REFERENCE_SOURCES.put(Language.JAVA, new JavaSourceGenerator());
		REFERENCE_SOURCES.put(Language.PYTHON, new PythonSourceGenerator());
		REFERENCE_SOURCES.put(Language.CSHARP, new CSharpSourceGenerator());
		REFERENCE_SOURCES.put(Language.SCALA, new ScalaSourceGenerator());
	}

	public static String getReferenceSource(Language language) {
		AbstractSourceGenerator generator = REFERENCE_SOURCES.get(language);
		if (generator != null) {
			return generator.getSource(1); // No speedup
		}
		return null;
	}

	protected AbstractKeyboardSourceCodeGenerator keyboardSourceCodeGenerator;
	protected AbstractMouseSourceCodeGenerator mouseSourceCodeGenerator;

	public AbstractSourceGenerator() {
		source = new StringBuffer();
		sourceScheduler = new TaskSourceScheduler();

		keyboardSourceCodeGenerator = buildKeyboardSourceCodeGenerator();
		mouseSourceCodeGenerator = buildMouseSourceCodeGenerator();
	}

	public final boolean submitTask(long time, String device, String action, int[] param) {
		if (!verify(device, action, param)) {
			return false;
		}

		return internalSubmitTask(time, device, action, param);
	}

	protected boolean internalSubmitTask(long time, String device, String action, int[] params) {
		String mid = "";
		if (device.equals("mouse")) {
			mid = mouseSourceCodeGenerator.getSourceCode(action, params);
		} else if (device.equals("keyBoard")) {
			mid = keyboardSourceCodeGenerator.getSourceCode(action, params);
		} else {
			return false;
		}

		return mid == null ? false : sourceScheduler.addTask(new SchedulingData<String>(time, getSourceTab() + mid + "\n"));
	}

	protected final boolean verify(String device, String action, int[] param) {
		return Arrays.asList("mouse", "keyBoard").contains(device);
	}

	public final void clear() {
		source.setLength(0);
		sourceScheduler.clear();
	}

	/**
	 * This method is called once in the constructor.
	 *
	 * @return an {@link AbstractMouseSourceCodeGenerator} to generate mouse source code.
	 */
	protected abstract AbstractMouseSourceCodeGenerator buildMouseSourceCodeGenerator();

	/**
	 * This method is called once in the constructor.
	 *
	 * @return an {@link AbstractMouseSourceCodeGenerator} to generate keyboard source code.
	 */
	protected abstract AbstractKeyboardSourceCodeGenerator buildKeyboardSourceCodeGenerator();

	/**
	 * @return the {@link Language} of generated source code.
	 */
	protected abstract Language getSourceLanguage();

	/**
	 * @return the amount of indentation required for the generated source code.
	 */
	public abstract String getSourceTab();

	public String getSource(float speedup) {
		String mainSource = sourceScheduler.getSource(speedup);
		if (mainSource == null) {
			LOGGER.severe("Unable to generate source...");
			mainSource = "";
		}

		StringBuffer sb = new StringBuffer();
		sb.append(BootStrapResources.getNativeLanguageTemplate(getSourceLanguage()));
		sb.append(mainSource);

		return sb.toString();
	}
}
