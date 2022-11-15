package core.webui.server.handlers.renderedobjects;

import java.util.ArrayList;
import java.util.List;

import core.userDefinedTask.internals.preconditions.ActiveWindowsInfoCondition;
import core.userDefinedTask.internals.preconditions.AlwaysMatchingStringCondition;
import core.userDefinedTask.internals.preconditions.ContainingStringMatchingCondition;
import core.userDefinedTask.internals.preconditions.ExactStringMatchCondition;
import core.userDefinedTask.internals.preconditions.RegexStringMatchingCondition;
import core.userDefinedTask.internals.preconditions.StringMatchingCondition;
import utilities.StringUtilities;

public class RenderedActiveWindowInfosPreconditions {

	private List<RenderedMatchingOptionSelection> activeWindowTitleMatchingTypes;
	private String activeWindowTitle;
	private List<RenderedMatchingOptionSelection> activeProcessNameMatchingTypes;
	private String activeProcessName;

	public static RenderedActiveWindowInfosPreconditions of(ActiveWindowsInfoCondition condition) {
		RenderedActiveWindowInfosPreconditions result = new RenderedActiveWindowInfosPreconditions();

		result.activeWindowTitleMatchingTypes = renderedStringMatchingConditionTypeSelection(condition.getTitleCondition());
		result.activeWindowTitle = renderedStringMatchingCondition(condition.getTitleCondition());

		result.activeProcessNameMatchingTypes = renderedStringMatchingConditionTypeSelection(condition.getProcessNameCondition());
		result.activeProcessName = renderedStringMatchingCondition(condition.getProcessNameCondition());
		return result;
	}

	private static List<RenderedMatchingOptionSelection> renderedStringMatchingConditionTypeSelection(StringMatchingCondition condition) {
		List<RenderedMatchingOptionSelection> result = new ArrayList<>(3);
		result.add(RenderedMatchingOptionSelection.CONTAINING);
		result.add(RenderedMatchingOptionSelection.EXACT_MATCH);
		result.add(RenderedMatchingOptionSelection.REGEX_MATCH);

		if (condition == AlwaysMatchingStringCondition.INSTANCE) {
			result.set(0, result.get(0).selected());
			return result;
		}
		if (condition instanceof ContainingStringMatchingCondition) {
			result.set(0, result.get(0).selected());
			return result;
		}
		if (condition instanceof ExactStringMatchCondition) {
			result.set(1, result.get(1).selected());
			return result;
		}
		if (condition instanceof RegexStringMatchingCondition) {
			result.set(2, result.get(2).selected());
			return result;
		}

		result.set(0, result.get(0).selected());
		return result;
	}

	private static String renderedStringMatchingCondition(StringMatchingCondition condition) {
		if (condition == AlwaysMatchingStringCondition.INSTANCE) {
			return "";
		}
		if (condition instanceof ContainingStringMatchingCondition) {
			String value = ((ContainingStringMatchingCondition) condition).getSubstring();
			return StringUtilities.escapeHtml(value);
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


	public List<RenderedMatchingOptionSelection> getActiveWindowTitleMatchingTypes() {
		return activeWindowTitleMatchingTypes;
	}
	public void setActiveWindowTitleMatchingTypes(List<RenderedMatchingOptionSelection> activeWindowTitleMatchingTypes) {
		this.activeWindowTitleMatchingTypes = activeWindowTitleMatchingTypes;
	}
	public String getActiveWindowTitle() {
		return activeWindowTitle;
	}
	public void setActiveWindowTitle(String activeWindowTitle) {
		this.activeWindowTitle = activeWindowTitle;
	}
	public List<RenderedMatchingOptionSelection> getActiveProcessNameMatchingTypes() {
		return activeProcessNameMatchingTypes;
	}
	public void setActiveProcessNameMatchingTypes(List<RenderedMatchingOptionSelection> activeProcessNameMatchingTypes) {
		this.activeProcessNameMatchingTypes = activeProcessNameMatchingTypes;
	}
	public String getActiveProcessName() {
		return activeProcessName;
	}
	public void setActiveProcessName(String activeProcessName) {
		this.activeProcessName = activeProcessName;
	}

	private RenderedActiveWindowInfosPreconditions() {}
}
