package core.userDefinedTask.manualBuild.steps;

import argo.jdom.JsonNode;
import core.controller.Core;
import core.userDefinedTask.manualBuild.ManuallyBuildStep;

public class MouseClickStep extends ManuallyBuildStep {

	private int mask;
	private int x;
	private int y;

	public static MouseClickStep of(int mask, int x, int y) {
		MouseClickStep result = new MouseClickStep();
		result.mask = mask;
		result.x = x;
		result.y = y;
		return result;
	}

	@Override
	public void execute(Core controller) throws InterruptedException {
		controller.mouse().click(mask, x, y);
	}

	@Override
	public String getDisplayString() {
		return String.format("mouse click %s at %s", DisplayTextUtil.mouseMaskToString(mask), DisplayTextUtil.coordinate(x, y));
	}

	public static MouseClickStep parseJSON(JsonNode node) {
		MouseClickStep result = new MouseClickStep();
		result.parse(node);
		return result;
	}

	@Override
	public String getJsonSignature() {
		return "mouse_click";
	}
}
