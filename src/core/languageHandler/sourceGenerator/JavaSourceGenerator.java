package core.languageHandler.sourceGenerator;

import java.util.logging.Logger;

import utilities.Function;
import core.scheduler.SchedulingData;

public class JavaSourceGenerator extends AbstractSourceGenerator {

	private static final Logger LOGGER = Logger.getLogger(JavaSourceGenerator.class.getName());

	public JavaSourceGenerator() {
		super();
		this.sourceScheduler.setSleepSource(new Function<Long, String>() {
			@Override
			public String apply(Long r) {
				return FOUR_TAB + "controller.blockingWait(" + r + ");\n";
			}
		});
	}

	@Override
	public boolean internalSubmitTask(long time, String device, String action, int[] param) {
		String mid = "";
		if (device.equals("mouse")) {
			if (action.equals("move")) {
				mid = "controller.mouse().move(" + param[0] + ", " + param[1] +");\n";
			} else if (action.equals("moveBy")) {
				mid = "controller.mouse().moveBy(" + param[0] + ", " + param[1] +");\n";
			} else if (action.equals("click")) {
				mid = "controller.mouse().click(" + param[0] + ");\n";
			} else if (action.equals("press")) {
				mid = "controller.mouse().press(" + param[0] + ");\n";
			} else if (action.equals("release")) {
				mid = "controller.mouse().release(" + param[0] + ");\n";
			} else {
				return false;
			}
		} else if (device.equals("keyBoard")) {
			if (action.equals("type")) {
				mid = "controller.keyBoard().type(" + param[0] + ");\n";
			} else if (action.equals("press")) {
				mid = "controller.keyBoard().press(" + param[0] + ");\n";
			} else if (action.equals("release")) {
				mid = "controller.keyBoard().release(" + param[0] + ");\n";
			} else {
				return false;
			}
		} else if (action.equals("wait")) {
			mid = "controller.blockingWait(" + param[0] + ");\n";
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
		sb.append("package core;\n");
		sb.append("import core.userDefinedTask.UserDefinedAction;\n");
		sb.append("import core.userDefinedTask.Tools;\n");
		sb.append("import core.controller.Core;\n");
		sb.append("import core.controller.MouseCore;\n");
		sb.append("import core.controller.KeyboardCore;\n");
		sb.append("import core.keyChain.KeyChain;\n");
		sb.append("import java.util.List;\n");
		sb.append("import static java.awt.event.KeyEvent.*;\n");
		sb.append("import static java.awt.event.InputEvent.BUTTON1_MASK;\n");
		sb.append("import static java.awt.event.InputEvent.BUTTON3_MASK;\n");
		sb.append("import utilities.swing.SwingUtil.OptionPaneUtil;\n");
		sb.append("import utilities.swing.SwingUtil.DialogUtil;\n");

		sb.append("public class CustomAction extends UserDefinedAction {\n");
		sb.append("    public void action(final Core c) throws InterruptedException {\n");
		sb.append("        KeyboardCore k = c.keyBoard();\n");
		sb.append("        MouseCore m = c.mouse();\n");
		sb.append("        List<Integer> invoker = this.invokingKeyChain.getKeys();\n");
		sb.append("        /*Begin generated code*/\n");
		sb.append(mainSource);
		sb.append("    }\n");
		sb.append("}");

		return sb.toString();
	}
}
