package core.userDefinedTask.manualBuild;

import java.util.Collections;
import java.util.List;

import core.controller.Core;
import core.userDefinedTask.UserDefinedAction;

public class ManuallyBuildAction extends UserDefinedAction {

	private List<ManuallyBuildStep> steps;

	protected ManuallyBuildAction(List<ManuallyBuildStep> steps) {
		this.steps = steps;
	}

	public static ManuallyBuildAction of(List<ManuallyBuildStep> steps) {
		return new ManuallyBuildAction(steps);
	}

	@Override
	public void action(Core controller) throws InterruptedException {
		for (ManuallyBuildStep step : steps) {
			step.execute(controller);
		}
	}

	public List<ManuallyBuildStep> getSteps() {
		return Collections.unmodifiableList(steps);
	}
}
