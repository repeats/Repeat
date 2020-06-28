package core.userDefinedTask.manualBuild.steps;

import argo.jdom.JsonNode;
import core.controller.Core;
import core.userDefinedTask.manualBuild.ManuallyBuildStep;
import utilities.KeyEventCodeToString;

public class KeyboardTypeKeyStep extends ManuallyBuildStep {

	private int key;

	public static KeyboardTypeKeyStep of(int key) {
		KeyboardTypeKeyStep result = new KeyboardTypeKeyStep();
		result.key = key;
		return result;
	}

	@Override
	public void execute(Core controller) throws InterruptedException {
		controller.keyBoard().type(key);
	}

	@Override
	public String getDisplayString() {
		return String.format("type key %s", KeyEventCodeToString.codeToString(key).toUpperCase());
	}

	public static KeyboardTypeKeyStep parseJSON(JsonNode node) {
		KeyboardTypeKeyStep result = new KeyboardTypeKeyStep();
		result.parse(node);
		return result;
	}

	@Override
	public String getJsonSignature() {
		return "keyboard_type_key";
	}
}
