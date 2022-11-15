package core.webui.server.handlers.renderedobjects;

import core.userDefinedTask.internals.preconditions.ActiveWindowsInfoCondition;
import core.userDefinedTask.internals.preconditions.AlwaysMatchingStringCondition;
import core.userDefinedTask.internals.preconditions.ExactStringMatchCondition;
import core.userDefinedTask.internals.preconditions.RegexStringMatchingCondition;
import core.userDefinedTask.internals.preconditions.StringMatchingCondition;
import utilities.StringUtilities;

public class RenderedActiveWindowInfosPreconditions {

	private String activeWindowTitle;
	private String activeProcessName;

	public static RenderedActiveWindowInfosPreconditions of(ActiveWindowsInfoCondition condition) {
		RenderedActiveWindowInfosPreconditions result = new RenderedActiveWindowInfosPreconditions();
		result.activeWindowTitle = renderedStringMatchingCondition(condition.getTitleCondition());
		result.activeProcessName = renderedStringMatchingCondition(condition.getProcessNameCondition());
		return result;
	}

	private static String renderedStringMatchingCondition(StringMatchingCondition condition) {
		if (condition == AlwaysMatchingStringCondition.INSTANCE) {
			return "";
		}
		if (condition instanceof RegexStringMatchingCondition) {
			String value = ((RegexStringMatchingCondition) condition).getRegex();
			return StringUtilities.escapeHtml(value);
		}
		if (condition instanceof ExactStringMatchCondition) {
			String value = ((ExactStringMatchCondition) condition).getValue();
			return StringUtilities.escapeHtml(value);
		}
		return "";
	}

	public String getActiveWindowTitle() {
		return activeWindowTitle;
	}
	public void setActiveWindowTitle(String activeWindowTitle) {
		this.activeWindowTitle = activeWindowTitle;
	}
	public String getActiveProcessName() {
		return activeProcessName;
	}
	public void setActiveProcessName(String activeProcessName) {
		this.activeProcessName = activeProcessName;
	}

	private RenderedActiveWindowInfosPreconditions() {}
}
