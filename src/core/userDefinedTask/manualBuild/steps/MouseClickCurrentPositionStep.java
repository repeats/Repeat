package core.userDefinedTask.manualBuild.steps;

import argo.jdom.JsonNode;
import core.controller.Core;
import core.userDefinedTask.manualBuild.ManuallyBuildStep;

public class MouseClickCurrentPositionStep extends ManuallyBuildStep {

	private int mask;

	public static MouseClickCurrentPositionStep of(int mask) {
		MouseClickCurrentPositionStep result = new MouseClickCurrentPositionStep();
		result.mask = mask;
		return result;
	}

	@Override
	public void execute(Core controller) throws InterruptedException {
		controller.mouse().click(mask);
	}

	@Override
	public String getDisplayString() {
		return String.format("mouse click %s at current position", DisplayTextUtil.mouseMaskToString(mask));
	}

	public static MouseClickCurrentPositionStep parseJSON(JsonNode node) {
		MouseClickCurrentPositionStep result = new MouseClickCurrentPositionStep();
		result.parse(node);
		return result;
	}

	@Override
	public String getJsonSignature() {
		return "mouse_click_current_position";
	}
}
