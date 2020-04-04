package core.userDefinedTask.manualBuild.steps;

import argo.jdom.JsonNode;
import core.controller.Core;
import core.userDefinedTask.manualBuild.ManuallyBuildStep;

public class ControllerDelayStep extends ManuallyBuildStep {

	private int delay;

	public static ControllerDelayStep of(int delay) {
		ControllerDelayStep result = new ControllerDelayStep();
		result.delay = delay;
		return result;
	}

	@Override
	public void execute(Core controller) throws InterruptedException {
		controller.delay(delay);
	}

	@Override
	public String getDisplayString() {
		return String.format("wait for %d milliseconds", delay);
	}

	public static ControllerDelayStep parseJSON(JsonNode node) {
		ControllerDelayStep result = new ControllerDelayStep();
		result.parse(node);
		return result;
	}

	@Override
	public String getJsonSignature() {
		return "controller_delay";
	}
}
