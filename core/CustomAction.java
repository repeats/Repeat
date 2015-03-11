package core;
import core.UserDefinedAction;
import java.awt.event.InputEvent;
public class CustomAction extends UserDefinedAction {
    public void action(final Core controller) throws InterruptedException {
        System.out.println("hello");
        controller.mouse().move(1186,500);
		controller.mouse().press(InputEvent.BUTTON1_DOWN_MASK);
        controller.mouse().move(1265,500);
		controller.blockingWait(20);
		controller.mouse().release(InputEvent.BUTTON1_DOWN_MASK);
    }
}