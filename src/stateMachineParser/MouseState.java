package stateMachineParser;

import java.util.Arrays;
import java.util.List;

public class MouseState extends StaticState {

	private static final List<String> possibleStates = Arrays.asList(new String[] { "move", "moveby", "click", "press", "release" });

	@Override
	protected boolean verifyInput(String input) {
		return possibleStates.contains(input);
	}
}
