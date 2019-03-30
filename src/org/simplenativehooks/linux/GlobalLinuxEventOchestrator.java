package org.simplenativehooks.linux;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GlobalLinuxEventOchestrator {

	private static final Logger LOGGER = Logger.getLogger(GlobalLinuxEventOchestrator.class.getName());
	private static final int WAIT_TIME_MS = 3000;

	private static final GlobalLinuxEventOchestrator INSTANCE = new GlobalLinuxEventOchestrator();

	private boolean stop = false;
	private Thread deviceScanner;
	private LinuxInputDeviceEventManager deviceManager;

	private Set<String> allDevices;
	private Set<String> deviceFiles;

	private GlobalLinuxEventOchestrator() {
		deviceManager = new LinuxInputDeviceEventManager();
		allDevices = new HashSet<>();
		deviceFiles = new HashSet<>();
	}

	public static GlobalLinuxEventOchestrator of() {
		return INSTANCE;
	}

	public final void start() {
		stop = false;
		deviceScanner = null;

		deviceScanner = new Thread() {
			@Override
			public void run() {
				while (!stop) {
					try {
						scanDevicesAndUpdate();
					} catch (Exception e) {
						LOGGER.log(Level.WARNING, "Encountered exception when scanning input devices and update.\n" + e.getMessage(), e);
					}
					try {
						Thread.sleep(WAIT_TIME_MS);
					} catch (InterruptedException e) {
						LOGGER.warning("Interrupted while idling between device scans.");
					}
				}
			}
		};
		deviceScanner.start();
	}

	private void scanDevicesAndUpdate() {
		List<ProcBusInputDevicesInfo> devices = ProcBusInputDevicesInfo.read();
		Set<String> updatedAllDeviceFiles = devices.stream().map(ProcBusInputDevicesInfo::getDeviceFile).collect(Collectors.toSet());
		if (updatedAllDeviceFiles.size() == allDevices.size()) {
			if (updatedAllDeviceFiles.containsAll(allDevices)) { // No new device.
				return;
			}
		}
		LOGGER.info("Detected changes in input devices...");
		allDevices.clear();
		allDevices.addAll(updatedAllDeviceFiles);

		List<String> mice = devices.stream().filter(ProcBusInputDevicesInfo::isMouse).map(ProcBusInputDevicesInfo::getDeviceFile).collect(Collectors.toList());
		List<String> keyboards = devices.stream().filter(ProcBusInputDevicesInfo::isKeyboard).map(ProcBusInputDevicesInfo::getDeviceFile).collect(Collectors.toList());
		Set<String> updatedDeviceFiles = new HashSet<>();
		updatedDeviceFiles.addAll(mice);
		updatedDeviceFiles.addAll(keyboards);

		for (String deviceFile : mice) {
			if (!deviceFiles.contains(deviceFile)) {
				deviceManager.addNewDevice(LinuxDeviceType.MOUSE_DEVICE, deviceFile);
			}
		}
		for (String deviceFile : keyboards) {
			if (!deviceFiles.contains(deviceFile)) {
				deviceManager.addNewDevice(LinuxDeviceType.KEYBOARD_DEVICE, deviceFile);
			}
		}

		deviceFiles.removeAll(updatedDeviceFiles);
		for (String deviceFile : deviceFiles) {
			try {
				deviceManager.removeDevice(deviceFile);
			} catch (InterruptedException e) {
				LOGGER.warning("Interrupted when removing device file " + deviceFile + ".");
			}
		}
		deviceFiles = updatedDeviceFiles;
	}

	public final void stop() {
		stop = true;
		try {
			deviceScanner.join();
		} catch (InterruptedException e) {
			LOGGER.warning("Interrupted while waiting for device scanning thread to end.");
		}
		try {
			deviceManager.removeAllDevices();
		} catch (InterruptedException e) {
			LOGGER.warning("Interrupted while removing all devices.");
		}
		deviceScanner = null;
	}
}
