package core.userDefinedTask.manualBuild.steps;

import argo.jdom.JsonNode;
import core.controller.Core;
import core.keyChain.KeyboardState;
import core.userDefinedTask.manualBuild.ManuallyBuildStep;
import utilities.KeyCodeToChar;

public class KeyboardPressKeyStep extends ManuallyBuildStep {

	private int key;

	public static KeyboardPressKeyStep of(int key) {
		KeyboardPressKeyStep result = new KeyboardPressKeyStep();
		result.key = key;
		return result;
	}

	@Override
	public void execute(Core controller) throws InterruptedException {
		controller.keyBoard().press(key);
	}

	@Override
	public String getDisplayString() {
		return String.format("press key %s", KeyCodeToChar.getCharForCode(key, KeyboardState.getDefault()).toUpperCase());
	}

	public static KeyboardPressKeyStep parseJSON(JsonNode node) {
		KeyboardPressKeyStep result = new KeyboardPressKeyStep();
		result.parse(node);
		return result;
	}

	@Override
	public String getJsonSignature() {
		return "keyboard_press_key";
	}
}
