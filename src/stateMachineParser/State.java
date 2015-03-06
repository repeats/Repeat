package stateMachineParser;

import java.util.HashMap;
import java.util.List;

public abstract class State {
	protected HashMap<String, State> inputMap;
	protected boolean isFinal;

	protected abstract boolean verifyInput(String input);

	protected abstract Object parseInput(String input);

	protected abstract String inputToKey(String input);

	protected void advance(String[] args, int position, List<Object> currentInputs) {
		String currentInput = args[position];

		if (verifyInput(currentInput)) {
			String nextKey = inputToKey(currentInput);
			if (inputMap.containsKey(nextKey)) {
				State next = inputMap.get(nextKey);
				currentInputs.add(parseInput(currentInput));
				next.advance(args, position + 1, currentInputs);
			} else {
				throw new IllegalStateException("Cannot find next state with input " + currentInput);
			}
		} else {
			throw new IllegalArgumentException("Wrong input for input " + currentInput);
		}
	}
}
