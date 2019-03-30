package org.simplenativehooks.linux;

import java.io.File;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.simplenativehooks.AbstractNativeHookEventProcessor;
import org.simplenativehooks.NativeHookGlobalEventPublisher;

import staticResources.NativeHookBootstrapResources;

class LinuxDeviceEventProcessor extends AbstractNativeHookEventProcessor {

	private static final Logger LOGGER = Logger.getLogger(LinuxDeviceEventProcessor.class.getName());

	private static final File EXECUTABLE_FILE = NativeHookBootstrapResources.getNativeHookExecutable();
	//E.g.: 'Ts:1544327169,Tus:649369,T:1,C:46,V:1'
	private static final Pattern EVENT_PATTERN = Pattern.compile("^Ts:([0-9]+?),Tus:([0-9]+?),T:([0-9]+?),C:([0-9]+?),V:(-?[0-9]+)$");

	private LinuxDeviceType deviceType;
	private String deviceFile;
	private Timestamp lastTime;

	protected LinuxDeviceEventProcessor(LinuxDeviceType deviceType, String deviceFile) {
		this.deviceType = deviceType;
		this.deviceFile = deviceFile;

		this.lastTime = new Timestamp(0, 0);
		setRunWithSudo();
	}

	@Override
	public final String getName() {
		return deviceFile;
	}

	@Override
	public File getExecutionDir() {
		return new File(".");
	}

	@Override
	public String[] getCommand() {
		return new String[] { EXECUTABLE_FILE.getAbsolutePath(), deviceFile };
	}

	@Override
	public void processStdout(String line) {
		Matcher m = EVENT_PATTERN.matcher(line);
		if (m.find()) {
			String secondString = m.group(1);
			String nanoSecondString = m.group(2);
			String typeString = m.group(3);
			String codeString = m.group(4);
			String valueString = m.group(5);

			long second, nanoSecond;
			int type, code, value;
			try {
				second = Long.parseLong(secondString);
				nanoSecond = Long.parseLong(nanoSecondString);

				type = Integer.parseInt(typeString);
				code = Integer.parseInt(codeString);
				value = Integer.parseInt(valueString);
			} catch (NumberFormatException e) {
				LOGGER.info(line);
				return;
			}

			Timestamp newTime = new Timestamp(second, nanoSecond);
			if (newTime.equals(lastTime) && deviceType.equals(LinuxDeviceType.MOUSE_DEVICE)) {
				LOGGER.fine("Ignore mouse event at the same timestamp.");
				return;
			}
			lastTime = newTime;

			if (deviceType.equals(LinuxDeviceType.MOUSE_DEVICE)) {
				NativeHookGlobalEventPublisher.of().publishMouseEvent(LinuxNativeMouseEvent.of(type, code, value));
			}
			if (deviceType.equals(LinuxDeviceType.KEYBOARD_DEVICE)) {
				NativeHookGlobalEventPublisher.of().publishKeyEvent(LinuxNativeKeyEvent.of(type, code, value));
			}
			return;
		}

		LOGGER.info(line);
	}

	@Override
	public void processStderr(String line) {
		LOGGER.info(line);
	}

	private static class Timestamp {
		long second;
		long nanoSecond;

		private Timestamp(long second, long nanoSecond) {
			this.second = second;
			this.nanoSecond = nanoSecond;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (nanoSecond ^ (nanoSecond >>> 32));
			result = prime * result + (int) (second ^ (second >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			Timestamp other = (Timestamp) obj;
			if (nanoSecond != other.nanoSecond) {
				return false;
			}
			if (second != other.second) {
				return false;
			}
			return true;
		}
	}
}
