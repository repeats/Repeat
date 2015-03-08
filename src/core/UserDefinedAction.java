package core;

public abstract class UserDefinedAction {

	public void executeAction(Core controller) {
		try {
			action(controller);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public abstract void action(Core controller) throws InterruptedException;
}