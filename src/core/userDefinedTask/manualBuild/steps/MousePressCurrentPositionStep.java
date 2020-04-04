package core.userDefinedTask.manualBuild.steps;

import argo.jdom.JsonNode;
import core.controller.Core;
import core.userDefinedTask.manualBuild.ManuallyBuildStep;

public class MousePressCurrentPositionStep extends ManuallyBuildStep {

	private int mask;

	public static MousePressCurrentPositionStep of(int mask) {
		MousePressCurrentPositionStep result = new MousePressCurrentPositionStep();
		result.mask = mask;
		return result;
	}

	@Override
	public void execute(Core controller) throws InterruptedException {
		controller.mouse().press(mask);
	}

	@Override
	public String getDisplayString() {
		return String.format("mouse press %s at current position", DisplayTextUtil.mouseMaskToString(mask));
	}

	public static MousePressCurrentPositionStep parseJSON(JsonNode node) {
		MousePressCurrentPositionStep result = new MousePressCurrentPositionStep();
		result.parse(node);
		return result;
	}

	@Override
	public String getJsonSignature() {
		return "mouse_press_current_position";
	}
}
