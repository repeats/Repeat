package commonTools;

import core.languageHandler.Language;

public class AreaClickerTool extends RepeatTool {

	public AreaClickerTool() {
		super();
		imports.add("import java.awt.Point;");
		imports.add("import utilities.Function;");
		imports.add("import core.Core;");
	}

	@Override
	protected boolean isSupported(Language language) {
		return language == Language.JAVA;
	}

	@Override
	protected String getBodySource(Language language) {
		if (language == Language.JAVA) {
			StringBuilder output = new StringBuilder();
			output.append(TWO_TAB + "Point topLeft = new Point(0,0);\n");
			output.append(TWO_TAB + "Point bottomRight = new Point(20,20);\n");
			output.append(TWO_TAB + "int row = 5;\n");
			output.append(TWO_TAB + "int column = 5;\n");
			output.append(TWO_TAB + "controller.mouse().moveArea(topLeft, bottomRight, row, column, new Function<Point, Void>() {\n");
			output.append(THREE_TAB + "public Void apply(Point r) {\n");
			output.append(FOUR_TAB + "System.out.println(\"At \" + r.x + \", \" + r.y);\n");
			output.append(FOUR_TAB + "return null;\n");
			output.append(THREE_TAB + "}\n");
			output.append(TWO_TAB + "});\n");

			return output.toString();
		}

		return null;
	}
}
