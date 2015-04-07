package frontEnd;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URISyntaxException;

import org.jnativehook.NativeHookException;




public class BlankClass {

	public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException, NativeHookException {
		System.out.println(KeyEvent.getExtendedKeyCodeForChar(' '));
		System.out.println(KeyEvent.VK_SPACE);
	}
}