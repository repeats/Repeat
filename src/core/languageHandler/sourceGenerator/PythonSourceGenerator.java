package core.languageHandler.sourceGenerator;

import java.util.logging.Logger;

import utilities.Function;
import core.SchedulingData;

public class PythonSourceGenerator extends AbstractSourceGenerator {

	private static final Logger LOGGER = Logger.getLogger(PythonSourceGenerator.class.getName());

	public PythonSourceGenerator() {
		super();
		this.sourceScheduler.setSleepSource(new Function<Long, String>() {
			@Override
			public String apply(Long r) {
				return TAB + "time.sleep(" + (r / 1000.0) + ")\n";
			}
		});
	}

	@Override
	public boolean internalSubmitTask(long time, String device, String action, int[] param) {
		String mid = "";
		if (device.equals("mouse")) {
			if (action.equals("move")) {
				mid = "repeat_lib.mouse_move(" + param[0] + ", " + param[1] +")\n";
			} else if (action.equals("moveBy")) {
				mid = "repeat_lib.mouse_move_by(" + param[0] + ", " + param[1] +")\n";
			} else if (action.equals("click")) {
				mid = "repeat_lib.mouse_click(" + param[0] + ")\n";
			} else if (action.equals("press")) {
				mid = "repeat_lib.mouse_press(" + param[0] + ")\n";
			} else if (action.equals("release")) {
				mid = "repeat_lib.mouse_release(" + param[0] + ")\n";
			} else {
				return false;
			}
		} else if (device.equals("keyBoard")) {
			if (action.equals("type")) {
				mid = "repeat_lib.key_type(" + param[0] + ")\n";
			} else if (action.equals("press")) {
				mid = "repeat_lib.key_press(" + param[0] + ")\n";
			} else if (action.equals("release")) {
				mid = "repeat_lib.key_release(" + param[0] + ")\n";
			} else {
				return false;
			}
		} else if (action.equals("wait")) {
			mid = "repeat_lib.blockingWait(" + param[0] + ")\n";
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
		sb.append("import repeat_lib\n");

		sb.append("if __name__ == \"__main__\":\n");
		sb.append("    #Begin generated code\n");
		sb.append(mainSource);

		return sb.toString();
	}

}
