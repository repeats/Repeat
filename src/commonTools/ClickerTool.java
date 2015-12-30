package commonTools;

import core.languageHandler.Language;

public class ClickerTool extends RepeatTool {

	@Override
	protected boolean isSupported(Language language) {
		return language == Language.JAVA;
	}

	@Override
	protected String getBodySource(Language language) {
		if (language == Language.JAVA) {
			StringBuilder output = new StringBuilder();
			output.append(TWO_TAB + "for (int i = 0; ; i++) {\n");
			output.append(THREE_TAB + "controller.mouse().leftClick();\n");
			output.append(TWO_TAB + "}\n");

			return output.toString();
		}

		return null;
	}
}
