package core.languageHandler.sourceGenerator;

import core.languageHandler.Language;
import core.userDefinedTask.manualBuild.steps.ControllerDelayStep;
import core.userDefinedTask.manualBuild.steps.KeyboardPressKeyStep;
import core.userDefinedTask.manualBuild.steps.KeyboardReleaseKeyStep;
import core.userDefinedTask.manualBuild.steps.KeyboardTypeKeyStep;
import core.userDefinedTask.manualBuild.steps.MouseClickCurrentPositionStep;
import core.userDefinedTask.manualBuild.steps.MouseMoveByStep;
import core.userDefinedTask.manualBuild.steps.MouseMoveStep;
import core.userDefinedTask.manualBuild.steps.MousePressCurrentPositionStep;
import core.userDefinedTask.manualBuild.steps.MouseReleaseCurrentPositionStep;
import utilities.Function;
import utilities.json.JSONUtility;

public class ManuallyBuildSourceGenerator extends InjectionSourceGenerator {

	public ManuallyBuildSourceGenerator() {
		super();
		this.sourceScheduler.setSleepSource(new Function<Long, String>() {
			@Override
			public String apply(Long r) {
				return JSONUtility.jsonToSingleLineString(ControllerDelayStep.of(r.intValue()).jsonize()) + "\n";
			}
		});
	}

	@Override
	protected Language getSourceLanguage() {
		return Language.MANUAL_BUILD;
	}

	@Override
	protected AbstractMouseSourceCodeGenerator buildMouseSourceCodeGenerator() {
		return new ManuallyBuildMouseSourceCodeGenerator();
	}

	@Override
	protected AbstractKeyboardSourceCodeGenerator buildKeyboardSourceCodeGenerator() {
		return new ManuallyBuildKeyboardSourceCodeGenerator();
	}

	@Override
	public String getSourceTab() {
		return "";
	}

	private class ManuallyBuildMouseSourceCodeGenerator extends AbstractMouseSourceCodeGenerator {

		@Override
		protected String move(int[] params) {
			return JSONUtility.jsonToSingleLineString(MouseMoveStep.of(params[0], params[1]).jsonize());
		}

		@Override
		protected String moveBy(int[] params) {
			return JSONUtility.jsonToSingleLineString(MouseMoveByStep.of(params[0], params[1]).jsonize());
		}

		@Override
		protected String click(int[] params) {
			return JSONUtility.jsonToSingleLineString(MouseClickCurrentPositionStep.of(params[0]).jsonize());
		}

		@Override
		protected String press(int[] params) {
			return JSONUtility.jsonToSingleLineString(MousePressCurrentPositionStep.of(params[0]).jsonize());
		}

		@Override
		protected String release(int[] params) {
			return JSONUtility.jsonToSingleLineString(MouseReleaseCurrentPositionStep.of(params[0]).jsonize());
		}
	}

	private class ManuallyBuildKeyboardSourceCodeGenerator extends AbstractKeyboardSourceCodeGenerator {

		@Override
		protected String type(int[] params) {
			return JSONUtility.jsonToSingleLineString(KeyboardTypeKeyStep.of(params[0]).jsonize());
		}

		@Override
		protected String press(int[] params) {
			return JSONUtility.jsonToSingleLineString(KeyboardPressKeyStep.of(params[0]).jsonize());
		}

		@Override
		protected String release(int[] params) {
			return JSONUtility.jsonToSingleLineString(KeyboardReleaseKeyStep.of(params[0]).jsonize());
		}
	}
}
