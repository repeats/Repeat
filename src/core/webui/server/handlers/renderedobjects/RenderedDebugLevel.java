package core.webui.server.handlers.renderedobjects;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class RenderedDebugLevel {
	public static final Level[] LEVELS = new Level[] {Level.FINE, Level.INFO, Level.WARNING, Level.SEVERE};

	private String name;
	private boolean selected;

	public static List<RenderedDebugLevel> of(Level selected) {
		return Arrays.asList(LEVELS).stream().map(l -> of(l, l == selected)).collect(Collectors.toList());
	}

	private static RenderedDebugLevel of(Level level, boolean selected) {
		RenderedDebugLevel output = new RenderedDebugLevel();
		output.name = level.getName();
		output.selected = selected;
		return output;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
