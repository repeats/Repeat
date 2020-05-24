package core.userDefinedTask.manualBuild;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import core.languageHandler.compiler.ManualBuildNativeCompiler;
import utilities.json.JSONUtility;

public class ManuallyBuildActionConstructor {

	private List<ManuallyBuildStep> steps;

	public static ManuallyBuildActionConstructor of() {
		return new ManuallyBuildActionConstructor(new ArrayList<>());
	}

	public static ManuallyBuildActionConstructor of(ManuallyBuildAction action) {
		return new ManuallyBuildActionConstructor(new ArrayList<>(action.getSteps()));
	}

	public List<ManuallyBuildStep> getSteps() {
		return Collections.unmodifiableList(steps);
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

	public String generateSource() {
		List<String> lines = Stream.concat(
				Stream.of(ManualBuildNativeCompiler.VERSION_PREFIX + ManualBuildNativeCompiler.VERSION),
				steps.stream().map(ManuallyBuildStep::jsonize).map(JSONUtility::jsonToSingleLineString)
			).collect(Collectors.toList());
		return String.join("\n", lines);
	}

	private ManuallyBuildActionConstructor(List<ManuallyBuildStep> steps) {
		this.steps = steps;
	}
}
