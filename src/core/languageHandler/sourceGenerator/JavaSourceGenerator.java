package core.languageHandler.sourceGenerator;

import utilities.Function;
import core.languageHandler.Language;

public class JavaSourceGenerator extends InjectionSourceGenerator {

	public JavaSourceGenerator() {
		super();
		this.sourceScheduler.setSleepSource(new Function<Long, String>() {
			@Override
			public String apply(Long r) {
				return TWO_TAB + "c.blockingWait(" + r + ");\n";
			}
		});
	}

	@Override
	protected Language getSourceLanguage() {
		return Language.JAVA;
	}

	@Override
	public String getSourceTab() {
		return TWO_TAB;
	}

	@Override
	protected AbstractMouseSourceCodeGenerator buildMouseSourceCodeGenerator() {
		return new JavaMouseSourceCodeGenerator();
	}

	@Override
	protected AbstractKeyboardSourceCodeGenerator buildKeyboardSourceCodeGenerator() {
		return new JavaKeyboardSourceCodeGenerator();
	}

	private class JavaMouseSourceCodeGenerator extends AbstractMouseSourceCodeGenerator {

		@Override
		protected String move(int[] params) {
			return "c.mouse().move(" + params[0] + ", " + params[1] +");";
		}

		@Override
		protected String moveBy(int[] params) {
			return "c.mouse().moveBy(" + params[0] + ", " + params[1] +");";
		}

		@Override
		protected String click(int[] params) {
			return "c.mouse().click(" + params[0] + ");";
		}

		@Override
		protected String press(int[] params) {
			return "c.mouse().press(" + params[0] + ");";
		}

		@Override
		protected String release(int[] params) {
			return "c.mouse().release(" + params[0] + ");";
		}
	}

	private class JavaKeyboardSourceCodeGenerator extends AbstractKeyboardSourceCodeGenerator {

		@Override
		protected String type(int[] params) {
			return "c.keyBoard().type(" + params[0] + ");";
		}

		@Override
		protected String press(int[] params) {
			return "c.keyBoard().press(" + params[0] + ");";
		}

		@Override
		protected String release(int[] params) {
			return "c.keyBoard().release(" + params[0] + ");";
		}
	}
}
