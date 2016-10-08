package core.languageHandler.sourceGenerator;

import utilities.Function;
import core.languageHandler.Language;

public class ScalaSourceGenerator extends InjectionSourceGenerator {

	public ScalaSourceGenerator() {
		super();
		this.sourceScheduler.setSleepSource(new Function<Long, String>() {
			@Override
			public String apply(Long r) {
				return TAB + "Thread.sleep(" + r + ")\n";
			}
		});
	}

	@Override
	protected Language getSourceLanguage() {
		return Language.SCALA;
	}

	@Override
	public String getSourceTab() {
		return TAB;
	}

	@Override
	protected AbstractMouseSourceCodeGenerator buildMouseSourceCodeGenerator() {
		return new ScalaMouseSourceCodeGenerator();
	}

	@Override
	protected AbstractKeyboardSourceCodeGenerator buildKeyboardSourceCodeGenerator() {
		return new ScalaKeyboardSourceCodeGenerator();
	}

	private class ScalaMouseSourceCodeGenerator extends AbstractMouseSourceCodeGenerator {

		@Override
		protected String move(int[] params) {
			return "m.move(" + params[0] + ", " + params[1] +")";
		}

		@Override
		protected String moveBy(int[] params) {
			return "m.moveBy(" + params[0] + ", " + params[1] +")";
		}

		@Override
		protected String click(int[] params) {
			return "m.click(" + params[0] + ")";
		}

		@Override
		protected String press(int[] params) {
			return "m.press(" + params[0] + ")";
		}

		@Override
		protected String release(int[] params) {
			return "m.release(" + params[0] + ")";
		}
	}

	private class ScalaKeyboardSourceCodeGenerator extends AbstractKeyboardSourceCodeGenerator {

		@Override
		protected String type(int[] params) {
			return "k.typeKeys(" + params[0] + ")";
		}

		@Override
		protected String press(int[] params) {
			return "k.press(" + params[0] + ")";
		}

		@Override
		protected String release(int[] params) {
			return "k.release(" + params[0] + ")";
		}
	}
}
