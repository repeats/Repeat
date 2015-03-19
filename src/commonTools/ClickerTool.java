package commonTools;

import core.recorder.Recorder;

public class ClickerTool extends RepeatTool {

	@Override
	protected boolean isSupported(int language) {
		return language == Recorder.JAVA_LANGUAGE;
	}

	@Override
	protected String getBodySource(int language) {
		if (language == Recorder.JAVA_LANGUAGE) {
			StringBuilder output = new StringBuilder();
			output.append(TWO_TAB + "for (int i = 0; ; i++) {\n");
			output.append(THREE_TAB + "controller.mouse().leftClick();\n");
			output.append(TWO_TAB + "}\n");

			return output.toString();
		}

		return null;
	}
}
