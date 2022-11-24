package frontEnd;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import core.userDefinedTask.internals.SharedVariablesPubSubManager;
import globalListener.GlobalListenerHookController;
import staticResources.BootStrapResources;

public class MainFrontEnd {

	private static final Logger LOGGER = Logger.getLogger(MainFrontEnd.class.getName());
	private static MainBackEndHolder backEnd;

	public static void run() {
		/*************************************************************************************/
		/********************************Extracting resources*********************************/
		try {
			BootStrapResources.extractResources();
		} catch (IOException | URISyntaxException e) {
			LOGGER.log(Level.SEVERE, "Cannot extract bootstrap resources.", e);
			System.exit(2);
		}
		/*************************************************************************************/
		/********************************Load configs****************************/
		backEnd = new MainBackEndHolder();
		backEnd.loadConfig(null);
		/*************************************************************************************/
		/********************************Initializing global hooks****************************/
		GlobalListenerHookController.Config hookConfig = GlobalListenerHookController.Config.Builder.of().useJavaAwtForMousePosition(backEnd.getConfig().isUseJavaAwtToGetMousePosition()).build();
		GlobalListenerHookController.of().initialize(hookConfig);
		SharedVariablesPubSubManager.get().start();
		/*************************************************************************************/
		/********************************Start main program***********************************/
		try {
			backEnd.keysManager.startGlobalListener();
		} catch (Exception e) {
			e.printStackTrace();
		}

		backEnd.configureMainHotkeys();
		/*************************************************************************************/
		backEnd.renderTaskGroup();

		/*************************************************************************************/
		backEnd.initializeLogging();
		backEnd.initiateBackEndActivities();
		backEnd.launchUI();
	}
}
