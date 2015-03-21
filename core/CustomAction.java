package core;
import core.controller.Core;
import core.UserDefinedAction;
public class CustomAction extends UserDefinedAction {
    public void action(final Core controller) throws InterruptedException {
        while (true) {
			controller.mouse().rightClick();
		}
    }
}
