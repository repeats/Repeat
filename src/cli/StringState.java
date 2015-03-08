package cli;

import java.util.List;

public class StringState extends ParseState {

	private final ParseState next;

	protected StringState(ParseState next) {
		this.next = next;
	}

	@Override
	protected ParseState parse(String arg, List<Object> parsed) {
		parsed.add(arg);
		return next;
	}
}
