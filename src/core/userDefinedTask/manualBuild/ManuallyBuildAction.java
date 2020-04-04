package core.userDefinedTask.manualBuild;

import java.util.List;

import core.controller.Core;
import core.userDefinedTask.UserDefinedAction;

public class ManuallyBuildAction extends UserDefinedAction {

	private List<ManuallyBuildStep> steps;

	protected ManuallyBuildAction(List<ManuallyBuildStep> steps) {
		this.steps = steps;
	}

	@Override
	public void action(Core controller) throws InterruptedException {
		for (ManuallyBuildStep step : steps) {
			step.execute(controller);
		}
	}
}
