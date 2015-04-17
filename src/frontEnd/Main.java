package frontEnd;

import java.awt.EventQueue;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import utilities.logging.ExceptionUtility;

public class Main {

	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

	public static void main(String[] args) throws FileNotFoundException {

		// Get the logger for "org.jnativehook" and set the level to warning.
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.WARNING);

		if (!GlobalScreen.isNativeHookRegistered()) {
			try {
				GlobalScreen.registerNativeHook();
			} catch (NativeHookException e) {
				LOGGER.severe("Cannot register native hook!");
				System.exit(1);
			}
		}

		/*********************************************************************************/

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					LOGGER.info("Successfully intialized application");

					frame.setVisible(true);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, ExceptionUtility.getStackTrace(e));
					System.exit(2);
				}
			}
		});
	}
}
