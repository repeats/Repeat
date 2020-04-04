package core.userDefinedTask.manualBuild.steps;

import argo.jdom.JsonNode;
import core.controller.Core;
import core.userDefinedTask.manualBuild.ManuallyBuildStep;

public class MouseMoveStep extends ManuallyBuildStep {

	private int x;
	private int y;

	public static MouseMoveStep of(int x, int y) {
		MouseMoveStep result = new MouseMoveStep();
		result.x = x;
		result.y = y;
		return result;
	}

	@Override
	public void execute(Core controller) throws InterruptedException {
		controller.mouse().move(x, y);
	}

	@Override
	public String getDisplayString() {
		return String.format("mouse move to %s", DisplayTextUtil.coordinate(x, y));
	}

	public static MouseMoveStep parseJSON(JsonNode node) {
		MouseMoveStep result = new MouseMoveStep();
		result.parse(node);
		return result;
	}

	@Override
	public String getJsonSignature() {
		return "mouse_move";
	}
}
