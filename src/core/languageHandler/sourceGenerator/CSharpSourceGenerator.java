package core.languageHandler.sourceGenerator;

import utilities.Function;
import core.languageHandler.Language;

public class CSharpSourceGenerator extends InjectionSourceGenerator {

	public CSharpSourceGenerator() {
		super();
		this.sourceScheduler.setSleepSource(new Function<Long, String>() {
			@Override
			public String apply(Long r) {
				return FOUR_TAB + "Thread.Sleep(" + r + ");\n";
			}
		});
	}

	@Override
	protected Language getSourceLanguage() {
		return Language.CSHARP;
	}

	@Override
	public String getSourceTab() {
		return FOUR_TAB;
	}

	@Override
	protected AbstractMouseSourceCodeGenerator buildMouseSourceCodeGenerator() {
		return new CSharpMouseSourceCodeGenerator();
	}

	@Override
	protected AbstractKeyboardSourceCodeGenerator buildKeyboardSourceCodeGenerator() {
		return new CSharpKeyboardSourceCodeGenerator();
	}

	private class CSharpMouseSourceCodeGenerator extends AbstractMouseSourceCodeGenerator {

		@Override
		protected String move(int[] params) {
			return "mouse.Move(" + params[0] + ", " + params[1] +");";
		}

		@Override
		protected String moveBy(int[] params) {
			return "mouse.MoveBy(" + params[0] + ", " + params[1] +");";
		}

		@Override
		protected String click(int[] params) {
			return "mouse.Click(" + params[0] + ");";
		}

		@Override
		protected String press(int[] params) {
			return "mouse.Press(" + params[0] + ");";
		}

		@Override
		protected String release(int[] params) {
			return "mouse.Release(" + params[0] + ");";
		}
	}

	private class CSharpKeyboardSourceCodeGenerator extends AbstractKeyboardSourceCodeGenerator {

		@Override
		protected String type(int[] params) {
			return "key.DoType(" + params[0] + ");";
		}

		@Override
		protected String press(int[] params) {
			return "key.Press(" + params[0] + ");";
		}

		@Override
		protected String release(int[] params) {
			return "key.Release(" + params[0] + ");";
		}

	}
}
