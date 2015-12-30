package commonTools;

import java.util.LinkedList;
import java.util.List;

import utilities.StringUtilities;
import core.languageHandler.Language;

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

	public String getSource(Language language) {
		if (isSupported(language)) {
			return "package core;\n"
					+ StringUtilities.join(imports, "\n") + "\n\n"
					+ getHeader(language) + getBodySource(language) + getFooter(language);
		} else {
			return "";
		}
	}

	protected String getHeader(Language language) {
		if (language == Language.JAVA) {
			return "public class CustomAction extends UserDefinedAction {\n"
					+ "    public void action(final Core controller) throws InterruptedException {\n";
		} else {
			return "";
		}
	}

	protected String getFooter(Language language) {
		if (language == Language.JAVA) {
			return "    }\n}";
		} else {
			return "";
		}
	}

	protected abstract boolean isSupported(Language language);
	protected abstract String getBodySource(Language language);
}
