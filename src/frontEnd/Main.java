package frontEnd;

import java.awt.EventQueue;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import staticResources.BootStrapResources;
import utilities.Function;
import utilities.logging.ExceptionUtility;
import utilities.logging.OutStream;

public class Main {

	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
	private static MainFrame createdFrame;

	public static void main(String[] args) throws FileNotFoundException {
		/*************************************************************************************/
		// Get the logger for "org.jnativehook" and set the level to WARNING to begin with.
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
		/*************************************************************************************/
		/********************************Extracting resources*********************************/
		try {
			BootStrapResources.extractResources();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Cannot extract bootstrap resources.", e);
			System.exit(1);
		}

		/*************************************************************************************/
		/********************************Defining backend activities**************************/
		final SwingWorker<Void, Void> backEndInitialization = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				if (createdFrame == null) {
					LOGGER.severe("Main frame is not created. Exitting...");
					System.exit(2);
				}

				createdFrame.backEnd.loadConfig(null);

				try {
					createdFrame.backEnd.keysManager.startGlobalListener();
				} catch (NativeHookException e) {
					e.printStackTrace();
				}
				createdFrame.backEnd.keysManager.setDisablingFunction(new Function<Void, Boolean>(){
					@Override
					public Boolean apply(Void r) {
						return createdFrame.hotkey.isVisible();
					}
				});

				createdFrame.backEnd.configureMainHotkeys();
				/*************************************************************************************/
				createdFrame.backEnd.renderTaskGroup();
				createdFrame.backEnd.renderTasks();

				PrintStream printStream = new PrintStream(new OutStream(createdFrame.taStatus));
				System.setOut(printStream);
				System.setErr(printStream);
				Logger.getLogger("").addHandler(new ConsoleHandler());
				/*************************************************************************************/

				createdFrame.backEnd.initiateBackEndActivities();
				return null;
			}
		};

		/*************************************************************************************/
		/********************************Start main program***********************************/
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainFrame frame = null;

				try {
					frame = new MainFrame();
					LOGGER.info("Successfully intialized application");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, ExceptionUtility.getStackTrace(e));
					System.exit(2);
				}

				if (frame == null) {
					LOGGER.info("Failed to initialize GUI.");
					System.exit(3);
				}

				createdFrame = frame;
				backEndInitialization.execute();
			}
		});
	}
}
