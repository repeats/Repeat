package core.userDefinedTask.manualBuild.steps;

import argo.jdom.JsonNode;
import core.controller.Core;
import core.userDefinedTask.manualBuild.ManuallyBuildStep;

public class KeyboardTypeStringStep extends ManuallyBuildStep {

	private String content;

	public static KeyboardTypeStringStep of(String content) {
		KeyboardTypeStringStep result = new KeyboardTypeStringStep();
		result.content = content;
		return result;
	}

	@Override
	public void execute(Core controller) throws InterruptedException {
		controller.keyBoard().type(content);
	}

	@Override
	public String getDisplayString() {
		return String.format("type string \"%s\"", content);
	}

	public static KeyboardTypeStringStep parseJSON(JsonNode node) {
		KeyboardTypeStringStep result = new KeyboardTypeStringStep();
		result.parse(node);
		return result;
	}

	@Override
	public String getJsonSignature() {
		return "keyboard_type_string";
	}
}
