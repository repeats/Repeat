package frontEnd;

import globalListener.GlobalKeyListener;

import java.io.IOException;
import java.net.URISyntaxException;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;

import utilities.Function;




public class BlankClass {

	public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException, NativeHookException {
		GlobalScreen.registerNativeHook();

		GlobalKeyListener x = new GlobalKeyListener();
		x.setKeyReleased(new Function<NativeKeyEvent, Boolean>() {
			@Override
			public Boolean apply(NativeKeyEvent d) {
				System.out.println(d.getKeyCode());
				return true;
			}
		});
		x.startListening();


		Thread.sleep(2000);
		GlobalScreen.unregisterNativeHook();
	}
}