package core.cli;

import java.util.List;

public abstract class ParseState {
	protected abstract ParseState parse(String arg, List<Object> parsed);
}
