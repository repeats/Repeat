package core.cli;

import java.util.List;

import utilities.NumberUtility;

public class NonNegativeIntegerState extends ParseState {

	private final ParseState next;

	protected NonNegativeIntegerState(ParseState next) {
		this.next = next;
	}

	@Override
	protected ParseState parse(String arg, List<Object> parsed) {
		if (NumberUtility.isInteger(arg)) {
			int val = Integer.parseInt(arg);
			if (val >= 0) {
				parsed.add(val);
				return next;
			} else {
			}
		}
		return null;
	}

}
