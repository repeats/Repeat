package core.userDefinedTask.manualBuild;

import java.util.ArrayList;
import java.util.List;

public class ManuallyBuildActionConstructor {

	private List<ManuallyBuildStep> steps;

	public static ManuallyBuildActionConstructor of() {
		return new ManuallyBuildActionConstructor();
	}

	public ManuallyBuildActionConstructor addStep(ManuallyBuildStep step) {
		steps.add(step);
		return this;
	}

	public ManuallyBuildActionConstructor removeStep(int index) {
		if (index >= 0 && index < steps.size()) {
			steps.remove(index);
		}
		return this;
	}

	public ManuallyBuildAction build() {
		return new ManuallyBuildAction(steps);
	}

	private ManuallyBuildActionConstructor() {
		steps = new ArrayList<>();
	}
}
