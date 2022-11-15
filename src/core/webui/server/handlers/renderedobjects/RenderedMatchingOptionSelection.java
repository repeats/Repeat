package core.webui.server.handlers.renderedobjects;

public class RenderedMatchingOptionSelection {
	private boolean chosen;
	private String htmlValue;
	private String displayedOption;

	public static final RenderedMatchingOptionSelection CONTAINING = new RenderedMatchingOptionSelection(
		false,
		"match_containing",
		"Containing"
	);

	public static final RenderedMatchingOptionSelection EXACT_MATCH = new RenderedMatchingOptionSelection(
		false,
		"match_exact",
		"Exact match"
	);

	public static final RenderedMatchingOptionSelection REGEX_MATCH = new RenderedMatchingOptionSelection(
		false,
		"match_regex",
		"Regex match"
	);

	public final RenderedMatchingOptionSelection selected() {
		return new RenderedMatchingOptionSelection(true, htmlValue, displayedOption);
	}

	public boolean isChosen() {
		return chosen;
	}
	public void setChosen(boolean chosen) {
		this.chosen = chosen;
	}
	public String getHtmlValue() {
		return htmlValue;
	}
	public void setHtmlValue(String htmlValue) {
		this.htmlValue = htmlValue;
	}
	public String getDisplayedOption() {
		return displayedOption;
	}
	public void setDisplayedOption(String displayedOption) {
		this.displayedOption = displayedOption;
	}

	private RenderedMatchingOptionSelection(boolean selected, String htmlValue, String displayedOption) {
		this.chosen = selected;
		this.htmlValue = htmlValue;
		this.displayedOption = displayedOption;
	}
}
