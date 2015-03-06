package stateMachineParser;

public abstract class StaticState extends State {

	@Override
	protected final Object parseInput(String input) {
		return input;
	}

	@Override
	protected final String inputToKey(String input) {
		return input;
	}
}
