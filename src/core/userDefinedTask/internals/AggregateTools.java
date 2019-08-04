package core.userDefinedTask.internals;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

public class AggregateTools implements ITools {

	private final Collection<ITools> tools;

	private AggregateTools(Collection<ITools> tools) {
		this.tools = tools;
	}

	public static AggregateTools of(Collection<ITools> tools) {
		if (tools.isEmpty()) {
			return new AggregateTools(Arrays.asList(NoopTools.of()));
		}
		return new AggregateTools(tools);
	}

	/**
	 * Returns the first clipboard.
	 */
	@Override
	public String getClipboard() {
		return tools.iterator().next().getClipboard();
	}

	@Override
	public boolean setClipboard(String data) {
		boolean result = true;
		for (ITools tool : tools) {
			result &= tool.setClipboard(data);
		}
		return result;
	}

	/**
	 * Returns the first non-empty output.
	 * Note that the command is executed on all tools.
	 */
	@Override
	public String execute(String command) {
		String output = "";
		for (ITools tool : tools) {
			String result = tool.execute(command);
			if (output.isEmpty()) {
				output = result;
			}
		}
		return output;
	}

	/**
	 * Returns the first non-empty output.
	 * Note that the command is executed on all tools.
	 */
	@Override
	public String execute(String command, File cwd) {
		String output = "";
		for (ITools tool : tools) {
			String result = tool.execute(command, cwd);
			if (output.isEmpty()) {
				output = result;
			}
		}
		return output;
	}

}
