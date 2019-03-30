package org.simplenativehooks.windows;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.simplenativehooks.AbstractNativeHookEventProcessor;
import org.simplenativehooks.NativeHookGlobalEventPublisher;

import staticResources.NativeHookBootstrapResources;

public class GlobalWindowsEventOchestrator extends AbstractNativeHookEventProcessor {

	private static final Logger LOGGER = Logger.getLogger(GlobalWindowsEventOchestrator.class.getName());

	private static final GlobalWindowsEventOchestrator INSTANCE = new GlobalWindowsEventOchestrator();
	private static final File EXECUTABLE_FILE = NativeHookBootstrapResources.getNativeHookExecutable();

	private static final Pattern KEY_PATTERN = Pattern.compile("^K:([0-9]+?),P:([0-9]+)$");
	private static final Pattern MOUSE_PATTERN = Pattern.compile("^M:(-?[0-9]+?),(-?[0-9]+?),P:([0-9]+)$");

	private GlobalWindowsEventOchestrator() {}

	public static GlobalWindowsEventOchestrator of() {
		return INSTANCE;
	}

	@Override
	public String getName() {
		return "Windows Hook";
	}

	@Override
	public File getExecutionDir() {
		return EXECUTABLE_FILE.getParentFile();
	}

	@Override
	public String[] getCommand() {
		return new String[] { EXECUTABLE_FILE.getPath() };
	}

	@Override
	public void processStdout(String line) {
		Matcher keyMatch = KEY_PATTERN.matcher(line);
		if (keyMatch.find()) {
			String codeString = keyMatch.group(1);
			String paramString = keyMatch.group(2);

			int code;
			int param;
			try {
				code = Integer.parseInt(codeString);
				param = Integer.parseInt(paramString);
			} catch (NumberFormatException e) {
				LOGGER.log(Level.SEVERE, "Unexpected number format when parsing hook output.", e);
				return;
			}

			NativeHookGlobalEventPublisher.of().publishKeyEvent(WindowsNativeKeyEvent.of(code, param));
			return;
		}

		Matcher mouseMatch = MOUSE_PATTERN.matcher(line);
		if (mouseMatch.find()) {
			String xString = mouseMatch.group(1);
			String yString = mouseMatch.group(2);
			String codeString = mouseMatch.group(3);

			int x, y, code;
			try {
				x = Integer.parseInt(xString);
				y = Integer.parseInt(yString);
				code = Integer.parseInt(codeString);
			} catch (NumberFormatException e) {
				LOGGER.log(Level.SEVERE, "Unexpected number format when parsing hook output.", e);
				return;
			}

			NativeHookGlobalEventPublisher.of().publishMouseEvent(WindowsNativeMouseEvent.of(x, y, code));
			return;
		}

		LOGGER.info(line);
	}

	@Override
	public void processStderr(String line) {
		LOGGER.info(line);
	}
}
