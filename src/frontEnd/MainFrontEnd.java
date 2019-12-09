package frontEnd;

import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.NativeHookException;

import core.userDefinedTask.internals.SharedVariablesPubSubManager;
import globalListener.GlobalListenerHookController;
import staticResources.BootStrapResources;
import utilities.logging.OutStream;

public class MainFrontEnd {

	private static final Logger LOGGER = Logger.getLogger(MainFrontEnd.class.getName());
	private static MainBackEndHolder backEnd;

	public static void run() {
		/*************************************************************************************/
		/********************************Extracting resources*********************************/
		try {
			BootStrapResources.extractResources();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Cannot extract bootstrap resources.", e);
			System.exit(2);
		}
		/*************************************************************************************/
		/********************************Initializing global hooks****************************/
		GlobalListenerHookController.of().initialize();
		SharedVariablesPubSubManager.get().start();
		/*************************************************************************************/
		/********************************Start main program***********************************/
		backEnd = new MainBackEndHolder();
		backEnd.loadConfig(null);

		try {
			backEnd.keysManager.startGlobalListener();
		} catch (NativeHookException e) {
			e.printStackTrace();
		}

		backEnd.configureMainHotkeys();
		/*************************************************************************************/
		backEnd.renderTaskGroup();

		PrintStream printStream = new PrintStream(new OutStream(backEnd.logHolder));
		System.setOut(printStream);
		System.setErr(printStream);
		Logger.getLogger("").addHandler(new ConsoleHandler());
		/*************************************************************************************/

		backEnd.initiateBackEndActivities();
		backEnd.launchUI();
	}
}
