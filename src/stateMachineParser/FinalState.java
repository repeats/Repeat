package stateMachineParser;

import java.util.List;

public abstract class FinalState extends State {
	protected abstract void action(List<Object> inputs);
}
