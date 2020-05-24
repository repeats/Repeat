package core.userDefinedTask.manualBuild.steps;

import argo.jdom.JsonNode;
import core.controller.Core;
import core.userDefinedTask.manualBuild.ManuallyBuildStep;

public class MouseMoveByStep extends ManuallyBuildStep {

	private int x;
	private int y;

	public static MouseMoveByStep of(int x, int y) {
		MouseMoveByStep result = new MouseMoveByStep();
		result.x = x;
		result.y = y;
		return result;
	}

	@Override
	public void execute(Core controller) throws InterruptedException {
		controller.mouse().moveBy(x, y);
	}

	@Override
	public String getDisplayString() {
		return String.format("mouse move by %s", DisplayTextUtil.coordinate(x, y));
	}

	public static MouseMoveByStep parseJSON(JsonNode node) {
		MouseMoveByStep result = new MouseMoveByStep();
		result.parse(node);
		return result;
	}

	@Override
	public String getJsonSignature() {
		return "mouse_move_by";
	}
}
