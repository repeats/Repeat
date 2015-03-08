package cli;

import java.util.List;

public abstract class TerminalState extends ParseState {

	protected abstract boolean execute(List<Object> parsed);

	@Override
	protected ParseState parse(String arg, List<Object> parsed) {
		return null;
	}

}
