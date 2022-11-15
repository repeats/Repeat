package core.background.loggers;

import java.util.logging.Logger;

import org.simplenativehooks.events.NativeMouseEvent;
import org.simplenativehooks.listeners.AbstractGlobalMouseListener;
import org.simplenativehooks.utilities.Function;

import core.controller.CoreProvider;
import globalListener.GlobalListenerFactory;
import utilities.natives.processes.NativeProcessUtil;
import utilities.natives.processes.NativeProcessUtil.NativeWindowInfo;

/**
 * Logs info of the active window (application on OSX) every time any mouse button is released, at most once every second.
 */
public class ActiveWindowInfosLogger {

	private static final Logger LOGGER = Logger.getLogger(ActiveWindowInfosLogger.class.getName());

	private static final long LOG_MAX_INTERVAL_MS = 1000;

	private final AbstractGlobalMouseListener mouseListener;
	private long lastLogged;
	private boolean enabled;

	public ActiveWindowInfosLogger(CoreProvider coreProvider) {
		mouseListener = GlobalListenerFactory.of().createGlobalMouseListener();
		mouseListener.setMouseReleased(new Function<NativeMouseEvent, Boolean>() {
			@Override
			public Boolean apply(NativeMouseEvent arg0) {
				if (!enabled) {
					return true;
				}

				long now = System.currentTimeMillis();
				long diff = Math.abs(now - lastLogged);
				if (diff <= LOG_MAX_INTERVAL_MS) {
					return true;
				}
				lastLogged = now;
				NativeWindowInfo info = NativeProcessUtil.getActiveWindowInfo();

				LOGGER.info("Active window title: " + info.getTitle());
				LOGGER.info("Active process name: " + info.getProcessName());
				return true;
			}
		});
		mouseListener.startListening();
	}

	public void stop() {
		mouseListener.stopListening();
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}
}
