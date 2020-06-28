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

	public ManuallyBuildActionConstructor addStep(int index, ManuallyBuildStep step) {
		index = index + 1;
		if (index >= steps.size()) {
			index = steps.size() - 1;
		}
		if (index < 0) { // Edge case where list is empty.
			index = 0;
		}

		steps.add(index, step);
		return this;
	}

	public ManuallyBuildActionConstructor removeStep(int index) {
		if (index >= 0 && index < steps.size()) {
			steps.remove(index);
		}
		return this;
	}

	public ManuallyBuildActionConstructor moveStepUp(int index) {
		if (index <= 0 || index >= steps.size()) {
			return this;
		}

		ManuallyBuildStep tmp = steps.get(index);
		steps.set(index, steps.get(index - 1));
		steps.set(index - 1, tmp);
		return this;
	}

	public ManuallyBuildActionConstructor moveStepDown(int index) {
		if (index < 0 || index >= steps.size()) {
			return this;
		}

		ManuallyBuildStep tmp = steps.get(index);
		steps.set(index, steps.get(index + 1));
		steps.set(index + 1, tmp);
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
