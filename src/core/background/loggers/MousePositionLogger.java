package core.background.loggers;

import java.awt.Point;
import java.util.logging.Logger;

import org.simplenativehooks.events.NativeKeyEvent;
import org.simplenativehooks.listeners.AbstractGlobalKeyListener;
import org.simplenativehooks.utilities.Function;

import com.sun.glass.events.KeyEvent;

import core.controller.Core;
import core.controller.CoreProvider;
import globalListener.GlobalListenerFactory;

/**
 * Logs the mouse position whenever the left Control key is pressed.
 */
public class MousePositionLogger {

	private static final Logger LOGGER = Logger.getLogger(MousePositionLogger.class.getName());

	private final AbstractGlobalKeyListener keyListener;
	private boolean enabled;

	public MousePositionLogger(CoreProvider coreProvider) {
		Core controller = coreProvider.getLocal();
		keyListener = GlobalListenerFactory.of().createGlobalKeyListener();
		keyListener.setKeyPressed(new Function<NativeKeyEvent, Boolean>() {
			@Override
			public Boolean apply(NativeKeyEvent e) {
				if (!enabled) {
					return true;
				}
				if (e.getKey() != KeyEvent.VK_CONTROL || e.getModifier() != NativeKeyEvent.Modifier.KEY_MODIFIER_LEFT) {
					return true;
				}

				Point p = controller.mouse().getPosition();
				LOGGER.info("Mouse position: " + p.getX() + ", " + p.getY());
				return true;
			}
		});
		keyListener.startListening();
	}

	public void stop() {
		keyListener.stopListening();
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}
}
