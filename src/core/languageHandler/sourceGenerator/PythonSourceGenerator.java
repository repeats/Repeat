package core.languageHandler.sourceGenerator;

import java.util.logging.Logger;

import staticResources.BootStrapResources;
import utilities.Function;
import core.languageHandler.Language;
import core.scheduler.SchedulingData;

public class PythonSourceGenerator extends AbstractSourceGenerator {

	private static final Logger LOGGER = Logger.getLogger(PythonSourceGenerator.class.getName());

	public PythonSourceGenerator() {
		super();
		this.sourceScheduler.setSleepSource(new Function<Long, String>() {
			@Override
			public String apply(Long r) {
				return TAB + "time.sleep(" + (r / 1000f) + ")\n";
			}
		});
	}

	@Override
	public boolean internalSubmitTask(long time, String device, String action, int[] param) {
		String mid = "";
		if (device.equals("mouse")) {
			if (action.equals("move")) {
				mid = "m.move(" + param[0] + ", " + param[1] +")\n";
			} else if (action.equals("moveBy")) {
				mid = "m.move_by(" + param[0] + ", " + param[1] +")\n";
			} else if (action.equals("click")) {
				mid = "m.click(" + param[0] + ")\n";
			} else if (action.equals("press")) {
				mid = "m.press(" + param[0] + ")\n";
			} else if (action.equals("release")) {
				mid = "m.release(" + param[0] + ")\n";
			} else {
				return false;
			}
		} else if (device.equals("keyBoard")) {
			if (action.equals("type")) {
				mid = "k.type(" + param[0] + ")\n";
			} else if (action.equals("press")) {
				mid = "k.press(" + param[0] + ")\n";
			} else if (action.equals("release")) {
				mid = "k.release(" + param[0] + ")\n";
			} else {
				return false;
			}
		} else if (action.equals("wait")) {
			mid = "time.sleep(" + (param[0] / 1000f) + ")\n";
		}

		return sourceScheduler.addTask(new SchedulingData<String>(time, TAB + mid));
	}

	@Override
	public String getSource() {
		String mainSource = sourceScheduler.getSource();
		if (mainSource == null) {
			LOGGER.severe("Unable to generate source...");
			mainSource = "";
		}

		StringBuffer sb = new StringBuffer();
		sb.append(BootStrapResources.getNativeLanguageTemplate(Language.PYTHON));
		sb.append(mainSource);

		return sb.toString();
	}
}
