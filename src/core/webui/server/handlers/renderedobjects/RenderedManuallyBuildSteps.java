package core.webui.server.handlers.renderedobjects;

import java.util.List;
import java.util.stream.Collectors;

import core.userDefinedTask.manualBuild.ManuallyBuildActionConstructor;

public class RenderedManuallyBuildSteps {
	private List<String> steps;

	private RenderedManuallyBuildSteps() {}

	public static RenderedManuallyBuildSteps fromManuallyBuildActionConstructor(ManuallyBuildActionConstructor constructor) {
		RenderedManuallyBuildSteps result = new RenderedManuallyBuildSteps();
		result.steps = constructor.getSteps().stream().map(s -> s.getDisplayString()).collect(Collectors.toList());
		return result;
	}

	public List<String> getSteps() {
		return steps;
	}
	public void setSteps(List<String> steps) {
		this.steps = steps;
	}
}
