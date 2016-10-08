package core.languageHandler.sourceGenerator;

import utilities.Function;
import core.languageHandler.Language;

public class PythonSourceGenerator extends AbstractSourceGenerator {

	public PythonSourceGenerator() {
		super();
		this.sourceScheduler.setSleepSource(new Function<Long, String>() {
			@Override
			public String apply(Long r) {
				return TAB + "time.sleep(" + (r / 1000f) + ")\n";
			}
		});
	}

	@Override
	protected Language getSourceLanguage() {
		return Language.PYTHON;
	}

	@Override
	public String getSourceTab() {
		return TAB;
	}

	@Override
	protected AbstractMouseSourceCodeGenerator buildMouseSourceCodeGenerator() {
		return new PythonMouseSourceCodeGenerator();
	}

	@Override
	protected AbstractKeyboardSourceCodeGenerator buildKeyboardSourceCodeGenerator() {
		return new PythonKeyboardSourceCodeGenerator();
	}

	private class PythonMouseSourceCodeGenerator extends AbstractMouseSourceCodeGenerator {

		@Override
		protected String move(int[] params) {
			return "m.move(" + params[0] + ", " + params[1] +")";
		}

		@Override
		protected String moveBy(int[] params) {
			return "m.move_by(" + params[0] + ", " + params[1] +")";
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

	private class PythonKeyboardSourceCodeGenerator extends AbstractKeyboardSourceCodeGenerator {

		@Override
		protected String type(int[] params) {
			return "k.type(" + params[0] + ")";
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
