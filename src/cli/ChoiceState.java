package cli;

import java.util.HashMap;
import java.util.List;

public class ChoiceState extends ParseState {

	private final HashMap<String, ParseState> choices;

	protected ChoiceState(HashMap<String, ParseState> choices) {
		this.choices = choices;
	}

	@Override
	protected ParseState parse(String arg, List<Object> parsed) {
		if (choices.containsKey(arg)) {
			parsed.add(arg);
			return choices.get(arg);
		} else {
			return null;
		}
	}
}
