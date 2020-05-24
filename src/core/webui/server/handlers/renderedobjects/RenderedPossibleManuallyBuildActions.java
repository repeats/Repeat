package core.webui.server.handlers.renderedobjects;

import java.util.List;

public class RenderedPossibleManuallyBuildActions {
	private List<String> actions;

	public static RenderedPossibleManuallyBuildActions of(List<String> possibleActions) {
		RenderedPossibleManuallyBuildActions result = new RenderedPossibleManuallyBuildActions();
		result.actions = possibleActions;
		return result;
	}

	public List<String> getActions() {
		return actions;
	}
}
