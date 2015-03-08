package core;

public class JavaSourceGenerator extends SourceGenerator {

	private static final String TAB = "    ";
	private static final String TWO_TAB = TAB + TAB;
	private static final String THREE_TAB = TWO_TAB + TAB;
	private static final String FOUR_TAB = THREE_TAB + TAB;

	@Override
	public boolean submitTask(long time, String device, String action, int[] param) {
		String prepend = "", append = "";
		if (this.verify(device, action, param)) {
			if (time >= 0) {
				prepend = TWO_TAB + "controller.wait(" + time + ", new Runnable() {\n"
							+ THREE_TAB + "public void run() {\n";
				append = THREE_TAB + "}\n" + TWO_TAB + "});\n";
			}
		} else {
			return false;
		}

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

		source.append(prepend + FOUR_TAB + mid + append);
		return true;
	}

	@Override
	public String getSource() {
		StringBuffer sb = new StringBuffer();
		sb.append("package core;\n");
		sb.append("import core.UserDefinedAction;\n");

		sb.append("public class CustomAction implements UserDefinedAction {\n");
		sb.append("    public void action(final Core controller) {\n");
		sb.append("        /*Begin generated code*/\n");
		sb.append(source.toString());
		sb.append("    }\n");
		sb.append("}");

		return sb.toString();
	}
}
