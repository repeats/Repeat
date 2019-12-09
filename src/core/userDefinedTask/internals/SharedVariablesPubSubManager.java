package core.userDefinedTask.internals;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SharedVariablesPubSubManager {

	private static final Logger LOGGER = Logger.getLogger(SharedVariablesPubSubManager.class.getName());

	private static final long POLL_TIMEOUT_MS = 2000;
	private static final SharedVariablesPubSubManager INSTANCE = new SharedVariablesPubSubManager();

	private BlockingQueue<SharedVariablesEvent> eventQueues = new LinkedBlockingQueue<>();
	private List<SharedVariablesSubscriber> subscribers = new LinkedList<>();
	private Thread processingThread;
	private boolean stopped;

	public static SharedVariablesPubSubManager get() {
		return INSTANCE;
	}

	public void addSubscriber(SharedVariablesSubscriber subscriber) {
		subscribers.add(subscriber);
	}

	public void removeSubscriber(SharedVariablesSubscriber subscriber) {
		subscribers.remove(subscriber);
	}

	public void notifyEvent(SharedVariablesEvent e) {
		if (stopped) {
			LOGGER.warning("Cannot notify new shared variables event since pubsub manager already stopped.");
			return;
		}

		if (!eventQueues.offer(e)) {
			LOGGER.warning("Dropping shared variable event " + e + ".");
		}
	}

	public synchronized void start() {
		if (processingThread != null) {
			return;
		}

		processingThread = new Thread() {
			@Override
			public void run() {
				while (!stopped) {
					processLoop();
				}
			}
		};
		processingThread.start();
	}

	private void processLoop() {
		SharedVariablesEvent event = null;
		try {
			event = eventQueues.poll(POLL_TIMEOUT_MS, TimeUnit.MILLISECONDS);
		} catch (InterruptedException ex) {
			LOGGER.log(Level.WARNING, "Polling for shared variable event.", ex);
		}
		if (event == null) {
			return;
		}

		for (SharedVariablesSubscriber subscriber : subscribers) {
			try {
				subscriber.processEvent(event);
			} catch (Exception ex) {
				LOGGER.log(Level.WARNING, "Processing shared variable event " + event + ".", ex);
			}
		}
	}

	public synchronized void stop() {
		if (processingThread == null) {
			return;
		}
		LOGGER.info("Terminating shared variables pubsub manager...");
		stopped = true;
		try {
			processingThread.join();
		} catch (InterruptedException e) {
			LOGGER.log(Level.WARNING, "Encountered error waiting for shared variables pubsub manager to stop.", e);
		}
		processingThread = null;
		LOGGER.info("Shared variables pubsub manager termninated.");
	}
}
