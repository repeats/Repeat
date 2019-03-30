package nativehooks.x11;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nativehooks.AbstractNativeHookEventProcessor;
import nativehooks.NativeHookGlobalEventPublisher;

public class X11NativeEventProcessor extends AbstractNativeHookEventProcessor {

	private static final Logger LOGGER = Logger.getLogger(X11NativeEventProcessor.class.getName());

	private static final Pattern KEY_EVENT = Pattern.compile("^K,T:([A-Z]),K:((\\-)?[0-9]+)$");
	private static final Pattern MOUSE_MOVE_EVENT = Pattern.compile("^M,T:M,X:((\\-)?[0-9]+),Y:((\\-)?[0-9]+)$");
	private static final Pattern MOUSE_BUTTON_EVENT = Pattern.compile("^M,T:([A-Z]),B:((\\-)?[0-9]+)$");

	private File executable;

	private X11NativeEventProcessor(File executable) {
		this.executable = executable;
	}

	static X11NativeEventProcessor of(File executable) {
		return new X11NativeEventProcessor(executable);
	}

	@Override
	public String getName() {
		return "X11 hook";
	}

	@Override
	public File getExecutionDir() {
		return executable.getParentFile();
	}

	@Override
	public String[] getCommand() {
		return new String[] { executable.getPath() };
	}

	@Override
	public void processStdout(String line) {
		Matcher keyMatch = KEY_EVENT.matcher(line);
		if (keyMatch.find()) {
			String type = keyMatch.group(1);
			String keyString = keyMatch.group(2);

			int key;
			try {
				key = Integer.parseInt(keyString);
			} catch (NumberFormatException e) {
				LOGGER.log(Level.WARNING, "Unexpected number format exception when parsing key value.", e);
				return;
			}
			NativeHookGlobalEventPublisher.of().publishKeyEvent(X11NativeKeyEvent.of(type, key));
			return;
		}

		Matcher mouseMovementMatch = MOUSE_MOVE_EVENT.matcher(line);
		if (mouseMovementMatch.find()) {
			String xString = mouseMovementMatch.group(1);
			String yString = mouseMovementMatch.group(3);

			int x, y;
			try {
				x = Integer.parseInt(xString);
				y = Integer.parseInt(yString);
			} catch (NumberFormatException e) {
				LOGGER.log(Level.WARNING, "Unexpected number format exception when parsing mouse coordinates.", e);
				return;
			}
			NativeHookGlobalEventPublisher.of().publishMouseEvent(X11NativeMouseEvent.of(x, y));
			return;
		}

		Matcher mouseButtonMatch = MOUSE_BUTTON_EVENT.matcher(line);
		if (mouseButtonMatch.find()) {
			String type = mouseButtonMatch.group(1);
			String buttonString = mouseButtonMatch.group(2);
			int button;
			try {
				button = Integer.parseInt(buttonString);
			} catch (NumberFormatException e) {
				LOGGER.log(Level.WARNING, "Unexpected number format exception when parsing mouse button.", e);
				return;
			}
			NativeHookGlobalEventPublisher.of().publishMouseEvent(X11NativeMouseEvent.of(type, button));
			return;
		}

		LOGGER.info(line);
	}

	@Override
	public void processStderr(String line) {
		LOGGER.info(line);
	}

}
