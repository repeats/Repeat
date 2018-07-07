package cli.server.utils;

public class EnumerationUtils {
	private EnumerationUtils() {}

	/**
	 * Converts an iterable of Strings into a readable list.
	 * Index starts from 0.
	 *
	 *  E.g. [aa,bb,cc,dd] will become
	 *  0) aa
	 *  1) bb
	 *  2) cc
	 *  3) dd
	 */
	public static String enumerate(Iterable<String> data) {
		StringBuilder builder = new StringBuilder();
		int i = 0;
		for (String s : data) {
			builder.append(String.format("%d) %s\n", i++, s));
		}
		return builder.toString();
	}
}
