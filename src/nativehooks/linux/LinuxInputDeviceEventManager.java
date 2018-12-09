package nativehooks.linux;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class LinuxInputDeviceEventManager {

	private static final Logger LOGGER = Logger.getLogger(LinuxInputDeviceEventManager.class.getName());

	private Map<String, LinuxDeviceEventProcessor> processors;

	public LinuxInputDeviceEventManager() {
		processors = new HashMap<>();
	}

	public void addNewDevice(LinuxDeviceType deviceType, String deviceFile) {
		LOGGER.info("Adding new device file " + deviceFile + " of type " + deviceType + ".");

		if (!new File(deviceFile).canRead()) {
			throw new IllegalArgumentException("Device file " + deviceFile + " is not readable.");
		}

		if (processors.containsKey(deviceFile)) {
			return;
		}

		LinuxDeviceEventProcessor processor = new LinuxDeviceEventProcessor(deviceType, deviceFile);
		processors.put(deviceFile, processor);
		processor.start();
	}

	public void removeAllDevices() throws InterruptedException {
		for (LinuxDeviceEventProcessor processor : processors.values()) {
			if (processor.isRunning()) {
				processor.stop();
			}
		}
		processors.clear();
	}

	public void removeDevice(String deviceFile) throws InterruptedException {
		if (!processors.containsKey(deviceFile)) {
			return;
		}

		LinuxDeviceEventProcessor processor = processors.remove(deviceFile);
		if (processor.isRunning()) {
			processor.stop();
		}
	}
}
