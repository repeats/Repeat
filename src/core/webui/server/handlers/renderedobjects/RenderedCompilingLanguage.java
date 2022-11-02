package core.webui.server.handlers.renderedobjects;

import java.util.UUID;

import core.languageHandler.Language;

public class RenderedCompilingLanguage {
	private String id;
	private String name;
	private boolean selected;
	private boolean disabled;

	public static RenderedCompilingLanguage forLanguage(Language language, boolean selected) {
		RenderedCompilingLanguage output = new RenderedCompilingLanguage();
		output.id = UUID.randomUUID().toString();
		output.name = language.toString();
		output.disabled = false;
		output.selected = selected;
		return output;
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

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
