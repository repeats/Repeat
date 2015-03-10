package core;
import core.UserDefinedAction;

public class CustomAction extends UserDefinedAction {
    public void action(final Core controller) throws InterruptedException {
        System.out.println("hello");
        controller.mouse().move(0, 0);
        controller.mouse().moveBy(300, 200);
        controller.mouse().moveBy(-200, 200);
        controller.blockingWait(1000);
    }
}