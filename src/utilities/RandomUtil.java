package utilities;

public class RandomUtil {

	/**
	 * Generate a random ID.
	 * It depends on the application nature and purpose that different strategies can be used.
	 * E.g. single threaded --> current milliseconds since epoch
	 * UUID and hash can also be feasible in certain cases
	 * @return a String represents the random ID
	 */
	public static String randomID() {
		return System.currentTimeMillis() + "";
	}

	private RandomUtil() {}
}
