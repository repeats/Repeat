package commonTools;

import java.util.LinkedList;
import java.util.List;

import utilities.StringUtilities;
import core.recorder.Recorder;

public abstract class RepeatTool {

	protected static final String TAB = "    ";
	protected static final String TWO_TAB = TAB + TAB;
	protected static final String THREE_TAB = TWO_TAB + TAB;
	protected static final String FOUR_TAB = THREE_TAB + TAB;

	protected List<String> imports;

	public RepeatTool() {
		imports = new LinkedList<>();
		imports.add("import core.UserDefinedAction;");
		imports.add("import core.controller.Core;");
	}

	public String getSource(int language) {
		if (isSupported(language)) {
			return "package core;\n"
					+ StringUtilities.join(imports, "\n") + "\n\n"
					+ getHeader(language) + getBodySource(language) + getFooter(language);
		} else {
			return null;
		}
	}

	protected String getHeader(int language) {
		switch (language) {
		case Recorder.JAVA_LANGUAGE:
			return "public class CustomAction extends UserDefinedAction {\n"
					+ "    public void action(final Core controller) throws InterruptedException {\n";
		default:
			return null;
		}
	}

	protected String getFooter(int language) {
		switch (language) {
		case Recorder.JAVA_LANGUAGE:
			return "    }\n}";
		default:
			return null;
		}

	}

	protected abstract boolean isSupported(int language);
	protected abstract String getBodySource(int language);
}
