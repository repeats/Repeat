package nativehooks.linux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utilities.FileUtility;

public class ProcBusInputDevicesInfo {

	private static final String INPUT_DEVICE_FILE = "/proc/bus/input/devices";

	private static final Pattern NAME_PATTERN = Pattern.compile("^N: Name=\"(.+)\"$");
	private static final Pattern HANDLERS_PATTERN = Pattern.compile("^H: Handlers=(.*?)(event[0-9]+)(.*)$");
	private static final Pattern EV_PATTERN = Pattern.compile("B: EV=(.*)$");

	// See linux/input-event-codes.h
	private static final int EV_SYN = 0x00;
	private static final int EV_KEY = 0x01;
	private static final int EV_REL = 0x02;
	private static final int EV_ABS = 0x03;
	private static final int EV_MSC = 0x04;
	private static final int EV_SW = 0x05;
	private static final int EV_LED = 0x11;
	private static final int EV_REP = 0x14;

	private String name;
	private String handler;
	private String ev;

	public ProcBusInputDevicesInfo(String name, String handler, String ev) {
		this.name = name;
		this.handler = handler;
		this.ev = evHexToBinary(ev);
	}

	public String getDeviceFile() {
		return "/dev/input/" + handler;
	}

	public boolean isMouse() {
		if (isKeyboard()) {
			return false;
		}

		if (evHasAllBits(EV_SW)) {
			return false;
		}

		if (!name.toLowerCase().contains("mouse")) {
			return false;
		}

		return evHasAllBits(EV_SYN, EV_KEY, EV_REL) || evHasAllBits(EV_SYN, EV_KEY, EV_ABS);
	}

	public boolean isKeyboard() {
		// EV for keyboard should be 0x120013.
		// See https://unix.stackexchange.com/questions/74903/explain-ev-in-proc-bus-input-devices-data
		return evHasOnlyBits(EV_SYN, EV_KEY, EV_MSC, EV_LED, EV_REP);
	}

	private boolean evHasAllBits(int...bits) {
		for (int bit : bits) {
			if (bit >= ev.length()) {
				return false;
			}

			if (ev.charAt(bit) != '1') {
				return false;
			}
		}

		return true;
	}

	private boolean evHasOnlyBits(int...bits) {
		int bitIndex = 0;

		Arrays.sort(bits);

		// When there are bits set outside the range that EV covers.
		if (bits[bits.length - 1] >= ev.length()) {
			return false;
		}

		// When EV length covers all the bits.
		for (int i = 0; i < ev.length(); i++) {
			char c = ev.charAt(i);

			boolean wantSet = bitIndex < bits.length && bits[bitIndex] == i;
			if (wantSet) {
				bitIndex++;
			}

			if (wantSet != (c == '1')) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Output in Little Endian. (LSB first).
	 */
	private static String evHexToBinary(String hex) {
		hex = hex.toUpperCase();

		StringBuilder b = new StringBuilder();
		for (int i = 0; i < hex.length(); i++) {
			int n = Integer.parseInt(hex.charAt(i) + "", 16);
		    String bin = Integer.toBinaryString(n);
		    while (bin.length() < 4) {
		    	bin = "0" + bin;
		    }
		    b.append(bin);
		}

		return b.reverse().toString();
	}

	private enum ParseState {
		READ_NAME,
		READ_HANDLER,
		READ_EV,
		WAIT_END;
	}

	/**
	 * Read the device file and collect information about all devices.
	 */
	public static List<ProcBusInputDevicesInfo> read() {
		List<ProcBusInputDevicesInfo> infos = new ArrayList<>();
		StringBuffer content = FileUtility.readFromFile(INPUT_DEVICE_FILE);
		if (content == null) {
			return infos;
		}

		String data = content.toString();
		ParseState s = ParseState.READ_NAME;
		String[] lines = data.split("\n");

		String name = "";
		String handler = "";
		String ev = "";

		for (String line : lines) {
			line = line.trim();
			Matcher m;
			switch (s) {
			case READ_NAME:
				m = NAME_PATTERN.matcher(line);
				if (m.find()) {
					name = m.group(1);
					s = ParseState.READ_HANDLER;
				}
				break;
			case READ_HANDLER:
				m = HANDLERS_PATTERN.matcher(line);
				if (m.find()) {
					handler = m.group(2).trim();
					s = ParseState.READ_EV;
				}
				break;
			case READ_EV:
				m = EV_PATTERN.matcher(line);
				if (m.find()) {
					ev = m.group(1);
					s = ParseState.WAIT_END;
				}
				break;
			case WAIT_END:
				if (line.isEmpty()) {
					infos.add(new ProcBusInputDevicesInfo(name, handler, ev));

					name = "";
					handler = "";
					ev = "";
					s = ParseState.READ_NAME;
				}
				break;
			default:
				throw new IllegalStateException("Unknown state " + s + " when parsing " + INPUT_DEVICE_FILE);
			}
		}
		if (!name.isEmpty() && !handler.isEmpty() && !ev.isEmpty()) {
			infos.add(new ProcBusInputDevicesInfo(name, handler, ev));
		}
		return infos;
	}
}
