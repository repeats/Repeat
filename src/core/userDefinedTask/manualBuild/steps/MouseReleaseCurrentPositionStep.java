package core.userDefinedTask.manualBuild.steps;

import argo.jdom.JsonNode;
import core.controller.Core;
import core.userDefinedTask.manualBuild.ManuallyBuildStep;

public class MouseReleaseCurrentPositionStep extends ManuallyBuildStep {

	private int mask;

	public static MouseReleaseCurrentPositionStep of(int mask) {
		MouseReleaseCurrentPositionStep result = new MouseReleaseCurrentPositionStep();
		result.mask = mask;
		return result;
	}

	@Override
	public void execute(Core controller) throws InterruptedException {
		controller.mouse().release(mask);
	}

	@Override
	public String getDisplayString() {
		return String.format("mouse release %s at current position", DisplayTextUtil.mouseMaskToString(mask));
	}

	public static MouseReleaseCurrentPositionStep parseJSON(JsonNode node) {
		MouseReleaseCurrentPositionStep result = new MouseReleaseCurrentPositionStep();
		result.parse(node);
		return result;
	}

	@Override
	public String getJsonSignature() {
		return "mouse_release_current_position";
	}
}
