package core.userDefinedTask.manualBuild.steps;

import argo.jdom.JsonNode;
import core.controller.Core;
import core.keyChain.KeyboardState;
import core.userDefinedTask.manualBuild.ManuallyBuildStep;
import utilities.KeyCodeToChar;

public class KeyboardReleaseKeyStep extends ManuallyBuildStep {

	private int key;

	public static KeyboardReleaseKeyStep of(int key) {
		KeyboardReleaseKeyStep result = new KeyboardReleaseKeyStep();
		result.key = key;
		return result;
	}

	@Override
	public void execute(Core controller) throws InterruptedException {
		controller.keyBoard().press(key);
	}

	@Override
	public String getDisplayString() {
		return String.format("release key %s", KeyCodeToChar.getCharForCode(key, KeyboardState.getDefault()).toUpperCase());
	}

	public static KeyboardReleaseKeyStep parseJSON(JsonNode node) {
		KeyboardReleaseKeyStep result = new KeyboardReleaseKeyStep();
		result.parse(node);
		return result;
	}

	@Override
	public String getJsonSignature() {
		return "keyboard_release_key";
	}
}
