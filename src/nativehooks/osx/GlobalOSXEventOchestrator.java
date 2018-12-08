package nativehooks.osx;

import java.awt.MouseInfo;
import java.awt.Point;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nativehooks.AbstractNativeHookEventOchestrator;
import nativehooks.NativeHookGlobalEventPublisher;
import staticResources.NativeHookBootstrapResources;

public class GlobalOSXEventOchestrator extends AbstractNativeHookEventOchestrator {
	private static final Logger LOGGER = Logger.getLogger(GlobalOSXEventOchestrator.class.getName());

	private static final GlobalOSXEventOchestrator INSTANCE = new GlobalOSXEventOchestrator();
	private static final File EXECUTABLE_FILE = NativeHookBootstrapResources.getNativeHookExecutable();;

	private static final Pattern MOUSE_EVENT = Pattern.compile("^E:([0-9]),X:([0-9]+?),Y:([0-9]+)$");
	private static final Pattern MOUSE_SCROLL_EVENT = Pattern.compile("^E:([0-9])$");
	private static final Pattern KEY_EVENT = Pattern.compile("^E:([0-9]),C:([0-9]+)$");
	private static final Pattern MODIFIER_EVENT = Pattern.compile("^E:([0-9]),C:([0-9]+?),M:([0-9]+)$");

	private GlobalOSXEventOchestrator() {}

	public static GlobalOSXEventOchestrator of() {
		return INSTANCE;
	}

	@Override
	public String getName() {
		return "OSX Hook";
	}

	@Override
	public File getExecutionDir() {
		return EXECUTABLE_FILE.getParentFile();
	}

	@Override
	public String getCommand() {
		return EXECUTABLE_FILE.getAbsolutePath();
	}

	@Override
	public void processStdout(String line) {
		Matcher mouseMatch = MOUSE_EVENT.matcher(line);
		if (mouseMatch.find()) {
			String codeString = mouseMatch.group(1);
			String xString = mouseMatch.group(2);
			String yString = mouseMatch.group(3);

			int code, x, y;
			try {
				code = Integer.parseInt(codeString);
				x = Integer.parseInt(xString);
				y = Integer.parseInt(yString);
			} catch (NumberFormatException e) {
				LOGGER.log(Level.WARNING, "Unexpected number format exception when parsing output.", e);
				return;
			}

			NativeHookGlobalEventPublisher.of().publishMouseEvent(OSXNativeMouseEvent.of(code, x, y));
			return;
		}

		Matcher mouseScrollMatch = MOUSE_SCROLL_EVENT.matcher(line);
		if (mouseScrollMatch.find()) {
			String codeString = mouseScrollMatch.group(1);
			int code;
			try {
				code = Integer.parseInt(codeString);
			} catch (NumberFormatException e) {
				LOGGER.log(Level.WARNING, "Unexpected number format exception when parsing output.", e);
				return;
			}

			Point p = MouseInfo.getPointerInfo().getLocation();
			NativeHookGlobalEventPublisher.of().publishMouseEvent(OSXNativeMouseEvent.of(code, p.x, p.y));
			return;
		}

		Matcher keyMatch = KEY_EVENT.matcher(line);
		if (keyMatch.find()) {
			String eventString = keyMatch.group(1);
			String codeString = keyMatch.group(2);

			int event, code;
			try {
				event = Integer.parseInt(eventString);
				code = Integer.parseInt(codeString);
			} catch (NumberFormatException e) {
				LOGGER.log(Level.WARNING, "Unexpected number format exception when parsing output.", e);
				return;
			}

			NativeHookGlobalEventPublisher.of().publishKeyEvent(OSXNativeKeyEvent.of(event, code, 0));
			return;
		}

		Matcher modifierMatch = MODIFIER_EVENT.matcher(line);
		if (keyMatch.find()) {
			String eventString = modifierMatch.group(1);
			String codeString = modifierMatch.group(2);
			String modifierString = modifierMatch.group(3);

			int event, code;
			long modifier;
			try {
				event = Integer.parseInt(eventString);
				code = Integer.parseInt(codeString);
				modifier = Long.parseLong(modifierString);
			} catch (NumberFormatException e) {
				LOGGER.log(Level.WARNING, "Unexpected number format exception when parsing output.", e);
				return;
			}

			NativeHookGlobalEventPublisher.of().publishKeyEvent(OSXNativeKeyEvent.of(event, code, modifier));
			return;
		}

		LOGGER.info(line);
	}

	@Override
	public void processStderr(String line) {
		LOGGER.info(line);
	}
}
