package utilities;


public final class NumberUtility {

	private NumberUtility() {}

	public static boolean equalDouble(double a, double b) {
		return Math.abs(a - b) < 0.00001d;
	}

	public static boolean equalFloat(float a, float b) {
		return Math.abs(a - b) < 0.00001f;
	}

	public static int getDigit(int number, int digit) {//0 means lsb
		String num = number + "";
		int lsb = num.length() - 1;
		lsb -= digit;
		if (lsb >= 0) {
			return num.charAt(lsb) - '0';
		} else {
			return -1;
		}
	}

	public static boolean isInteger(double input) {
		return (input == Math.floor(input)) && !Double.isInfinite(input);
	}

	public static boolean isPositiveInteger(String input) {
		return isInteger(input) && Integer.parseInt(input) > 0;
	}

	public static boolean isNonNegativeInteger(String input) {
		return isInteger(input) && Integer.parseInt(input) >= 0;
	}

	public static boolean isInteger(String input) {
		if (input == null) {
			return false;
		}

		if (input.length() == 0) {
			return false;
		}

		input = input.replaceAll(",", "");

		if (input.startsWith("-")) {
			input = input.substring(1);
		}

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c > '9' || c < '0') {
				return false;
			}
		}

		return true;
	}

	public static boolean isDouble(String input) {
		if (StringUtilities.countOccurrence(input, '.') > 1) {
			return false;
		}
		input = input.replaceAll("\\.", "");

		return isInteger(input);
	}

	public static boolean inRange(int a, int lower, int upper) {
		return (a >= lower) && (a <= upper);
	}

	public static boolean inRange(double a, double lower, double upper) {
		return (a >= lower) && (a <= upper);
	}
}
