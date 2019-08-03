package core.ipc.repeatClient.repeatPeerClient;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractRepeatsClientStoppableThread implements Runnable {

	private static final Logger LOGGER = Logger.getLogger(AbstractRepeatsClientStoppableThread.class.getName());

	private boolean stopped;
	protected ResponseManager responseManager;

	protected AbstractRepeatsClientStoppableThread(ResponseManager responseManager) {
		this.responseManager = responseManager;
	}

	@Override
	public final void run() {
		while (!stopped) {
			try {
				processLoop();
			} catch (InterruptedException ie) {
				LOGGER.log(Level.WARNING, "Interrupted exception when running process loop.", ie);
			} catch (IOException e) {
				LOGGER.log(Level.WARNING, "Encountered socket exception when running process loop. Terminating loop.", e);
				stopped = true;
				break;
			}
		}
	}

	protected abstract void processLoop() throws IOException, InterruptedException;

	protected final void stop() {
		stopped = true;
	}
}
