package core.webui.server.handlers.renderedobjects;

import core.keyChain.TaskActivationConstructor;
import core.userDefinedTask.UserDefinedAction;

public class RenderedDetailedUserDefinedAction {
	private String id;
	private String name;
	private String isEnabled;
	private RenderedTaskActivation activation;
	private RenderedUserDefinedActionStatistics statistics;

	private RenderedDetailedUserDefinedAction() {}

	public static RenderedDetailedUserDefinedAction fromUserDefinedAction(UserDefinedAction action, TaskActivationConstructor activationConstructor) {
		RenderedDetailedUserDefinedAction result = new RenderedDetailedUserDefinedAction();
		result.id = action.getActionId();
		result.name = action.getName();
		result.isEnabled = action.isEnabled() + "";
		result.activation = RenderedTaskActivation.fromActivation(activationConstructor);
		result.statistics = RenderedUserDefinedActionStatistics.fromUserDefinedActionStatistics(action.getStatistics());
		return result;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public RenderedTaskActivation getActivation() {
		return activation;
	}
	public void setActivation(RenderedTaskActivation activation) {
		this.activation = activation;
	}
	public RenderedUserDefinedActionStatistics getStatistics() {
		return statistics;
	}
	public void setStatistics(RenderedUserDefinedActionStatistics statistics) {
		this.statistics = statistics;
	}
	public String getIsEnabled() {
		return isEnabled;
	}
	public void setIsEnabled(String isEnabled) {
		this.isEnabled = isEnabled;
	}
}
