package core.webui.server.handlers.renderedobjects;

import core.keyChain.KeyChain;
import core.userDefinedTask.UserDefinedAction;
import utilities.DateUtility;

public class RenderedUserDefinedAction {
	private String name;
	private String activation;
	private String enabled;
	private long useCount;
	private String lastUsed;

	public static RenderedUserDefinedAction fromUserDefinedAction(UserDefinedAction action) {
		RenderedUserDefinedAction output = new RenderedUserDefinedAction();
		output.setName(action.getName());
		KeyChain representative = action.getRepresentativeHotkey();
		String activation = "None";
		if (representative != null && !representative.isEmpty()) {
			activation = representative.toString();
		}
		output.setActivation(activation);
		output.setEnabled(action.isEnabled() + "");
		output.setUseCount(action.getStatistics().getCount());

		String lastUsed = DateUtility.calendarToDateString(action.getStatistics().getLastUse());
		output.setLastUsed(lastUsed == null ? "" : lastUsed);
		return output;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getActivation() {
		return activation;
	}

	public void setActivation(String activation) {
		this.activation = activation;
	}

	public String getEnabled() {
		return enabled;
	}

	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}

	public long getUseCount() {
		return useCount;
	}

	public void setUseCount(long useCount) {
		this.useCount = useCount;
	}

	public String getLastUsed() {
		return lastUsed;
	}

	public void setLastUsed(String lastUsed) {
		this.lastUsed = lastUsed;
	}
}
