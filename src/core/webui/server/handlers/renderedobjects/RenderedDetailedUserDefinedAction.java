package core.webui.server.handlers.renderedobjects;

import core.keyChain.TaskActivationConstructor;
import core.userDefinedTask.UserDefinedAction;

public class RenderedDetailedUserDefinedAction {
	private String id;
	private String name;
	private String isEnabled;
	private RenderedTaskActivation activation;
	private String hasStatistics;
	private RenderedUserDefinedActionStatistics statistics;

	private RenderedDetailedUserDefinedAction() {}

	public static RenderedDetailedUserDefinedAction fromHotkey(String id, String name, TaskActivationConstructor activationConstructor) {
		RenderedDetailedUserDefinedAction result = new RenderedDetailedUserDefinedAction();
		result.id = id;
		result.name = name;
		result.isEnabled = true + "";
		result.activation = RenderedTaskActivation.fromActivation(activationConstructor);
		result.hasStatistics = false + "";
		result.statistics = null;
		return result;
	}

	public static RenderedDetailedUserDefinedAction fromUserDefinedAction(UserDefinedAction action, TaskActivationConstructor activationConstructor) {
		RenderedDetailedUserDefinedAction result = new RenderedDetailedUserDefinedAction();
		result.id = action.getActionId();
		result.name = action.getName();
		result.isEnabled = action.isEnabled() + "";
		result.activation = RenderedTaskActivation.fromActivation(activationConstructor);
		result.hasStatistics = true + "";
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
	public String getHasStatistics() {
		return hasStatistics;
	}
	public void setHasStatistics(String hasStatistics) {
		this.hasStatistics = hasStatistics;
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
