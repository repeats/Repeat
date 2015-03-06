package stateMachineParser;

import java.util.HashMap;

public class StartState extends State {

	protected StartState() {
		this.inputMap = new HashMap<>();
		this.inputMap.put("mouse", new MouseState());
		this.inputMap.put("key", new KeyState());
	}

	@Override
	protected boolean verifyInput(String input) {
		return input.equals("mouse") || input.equals("key");
	}

	@Override
	protected Object parseInput(String input) {
		return input;
	}

	@Override
	protected String inputToKey(String input) {
		return input;
	}
}
