package utilities;

import java.util.Arrays;
import java.util.Iterator;

public class StringUtilities {
	/**
	 * Join an iterable of string to one string with joiner. If an element of
	 * the iterable contains nothing or only space, it will be ignored
	 * 
	 * @param fields
	 *            iterable of string elements that will be joined.
	 * @param joiner
	 *            delimiter between each element
	 * @return One string resulted from the elements joined with joiner
	 */
	public static String join(Iterable<String> fields, String joiner) {
		StringBuilder builder = new StringBuilder();
		Iterator<String> iter = fields.iterator();
		while (iter.hasNext()) {
			String next = iter.next();
			boolean valid = next.replaceAll(" ", "").length() != 0;
			if (valid) {
				builder.append(next);
			}
			if (!iter.hasNext()) {
				break;
			}
			if (valid) {
				builder.append(joiner);
			}
		}
		return builder.toString();
	}

	/**
	 * Join an array of string to one string with joiner. If an element of the
	 * array contains nothing or only space, it will be ignored
	 * 
	 * @param data
	 *            array of string elements that will be joined.
	 * @param joiner
	 *            delimiter between each element
	 * @return One string resulted from the elements joined with joiner
	 */
	public static String join(String[] data, String joiner) {
		return join(Arrays.asList(data), joiner);
	}
}
