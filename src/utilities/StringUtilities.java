package utilities;

import java.awt.Color;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.apache.commons.text.StringEscapeUtils;

/**
 * Provide static interface to utilities
 *
 * @author HP
 *
 */
public class StringUtilities {

	/**
	 * Returns whether a string is null or empty.
	 */
	public static boolean isNullOrEmpty(String s) {
		return s == null || s.isEmpty();
	}

	/**
	 * Convert a color to its hex representation. E.g. red --> #ff0000
	 * @param color
	 * @return hex string represents the input color
	 */
	public static String getHexString(Color color) {
		String hex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
		return hex;
	}

	/**
	 * Join an iterable of string to one string with joiner. If an element
	 * of the iterable contains nothing or only space, it will be ignored
	 * @param fields iterable of string elements that will be joined.
	 * @param joiner delimiter between each element
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
	 * Join an array of string to one string with joiner. If an element
	 * of the array contains nothing or only space, it will be ignored
	 * @param data array of string elements that will be joined.
	 * @param joiner delimiter between each element
	 * @return One string resulted from the elements joined with joiner
	 */
	public static String join(String[] data, String joiner) {
		return join(Arrays.asList(data), joiner);
	}

	/**
	 * Transform string from the format "quick-brown-fox" into "quickBrownFox"
	 * @param input string in the format "quick-brown-fox"
	 * @return string in the format "quickBrownFox"
	 */
	public static String joinDash(String input) {
		String[] temp = input.split("-");
		for (int i = 1; i < temp.length; i++) {
			temp[i] = upperCaseFirstChar(temp[i]);
		}
		return join(temp, "");
	}

	/**
	 * Convert the first character of a string to upper case
	 * @param input a string
	 * @return the same string with first character capitalized
	 */
	public static String upperCaseFirstChar(String input) {
		String[] temp = input.split(" ");
		for (int i = 0; i < temp.length; i++) {
			temp[i] = Character.toUpperCase(temp[i].charAt(0)) + temp[i].substring(1);
		}
		return join(temp, " ");
	}

	/**
	 * Get the component of the string after split using the splitter (regex splitter)
	 * @param input input string
	 * @param splitter regex splitter
	 * @param index index of the component that will be selected. Negative number indicates a wrap around
	 * @return the component indicated by index after the string has been split using splitter
	 */
	public static String getComponent(String input, String splitter, int index) {
		String[] split = input.split(splitter);
		if (index < 0) {
			index += split.length;
		}
		return split[index];
	}

	/**
	 * Remove last characters of a string
	 * @param input the string that will have last characters removed
	 * @param amount the amount of characters that will be removed
	 * @return the string with amount of last characters removed
	 */
	public static String removeLast(String input, int amount) {
		if (input.length() >= amount) {
			return input.substring(0, input.length() - amount);
		} else {
			return input;
		}
	}

	/**
	 * Capitalize the first character of the string if possible
	 * @param input any string
	 * @return the input string with first character in upper case. Return same input if the first character is not a letter.
	 */
	public static String upperCaseFirst(String input) {
		if (input.charAt(0) >= 'a' && input.charAt(0) <= 'z') {
			return ("" + input.charAt(0)).toUpperCase() + input.substring(1);
		} else {
			return input;
		}
	}

	/**
	 * Count occurrence of a character in a String
	 * @param input a String
	 * @param c character to count
	 * @return number of occurrence of the character in the string. Return 0 if input is null.
	 */
	public static int countOccurrence(String input, char c) {
		if (input == null) {
			return 0;
		}

		int count = 0;
		for (int i = 0; i < input.length(); i++) {
			count = (input.charAt(i) == c) ? (count + 1) : count;
		}

		return count;
	}

	/**
	 * Verify that the list of substrings appear in the order provided in a string
	 * @param input reference string
	 * @param subStrings list of substrings that need to be verified
	 * @return -1 if all substrings are in order. Otherwise return the index at which substring is not in order
	 */
	public static int verifyOrder(String input, String[] subStrings) {
		int prev = -1;

		for (int i = 0; i < subStrings.length; i++) {
			int current = input.indexOf(subStrings[i]);
			if (current < prev) {
				return i;
			} else {
				prev = current;
			}
		}

		return -1;
	}

	/**
	 * Calculate the Levenshtein distance between two strings using dynamic programming.
	 * This implementation is based on the pseudocode presented on Wikipedia.
	 *
	 * @param l first string
	 * @param r second string
	 * @return the Levenshtein distance between two strings
	 */
	public static int levenshteinDistance(String l, String r) {
		if (l == null && r == null) {
			return 0;
		}
		if (l == null) {
			return r.length();
		}
		if (r == null) {
			return l.length();
		}

		if (l.isEmpty() && r.isEmpty()) {
			return 0;
		}
		if (l.isEmpty()) {
			return r.length();
		}
		if (r.isEmpty()) {
			return l.length();
		}

		int m = l.length();
		int n = r.length();

		int[][] d = new int[m+1][n+1];
		for (int i = 0; i < m; i++) {
			d[i][0] = i;
		}

		for (int j = 0; j < n; j++) {
			d[0][j] = j;
		}

		for (int i = 1; i <= m; i++) {
			for (int j = 1; j <= n; j++) {
				char cl = l.charAt(i-1);
				char cr = r.charAt(j-1);

				int cost = cl == cr ? 0 : 1;

				d[i][j] = Math.min(
							Math.min(d[i-1][j] + 1, d[i][j-1] + 1),
							d[i-1][j-1] + cost);

				// Uncomment this for Optimal Alignment String Distance calculation
				// Transposition
//				if (i > 1 && j > 1 && cl == r.charAt(j-2) && l.charAt(i-2) == cr) {
//					d[i][j] = Math.min(d[i][j], d[i-2][j-2]);
//				}
			}
		}

		return d[m][n];
	}

	private static final Pattern CAMEL_CASE_TO_SNAKE = Pattern.compile("([a-z])([A-Z]+)");

	/**
	 * Simple conversion from snake case to camel case.
	 * This does not take into account special cases like
	 * "a__3".
	 */
	public static String toCamelCase(String snakeCase) {
		StringBuilder sb = new StringBuilder(snakeCase);
		for (int i = 0; i < sb.length(); i++) {
		    if (sb.charAt(i) == '_') {
		        sb.deleteCharAt(i);
		        sb.replace(i, i+1, String.valueOf(Character.toUpperCase(sb.charAt(i))));
		    }
		}

		return sb.toString();
	}

	/**
	 * Simple conversion from camel case to snake case.
	 * This does not take into account special cases like
	 * "AA3" or "A__A3".
	 */
	public static String toSnakeCase(String camelCase) {
		return CAMEL_CASE_TO_SNAKE.matcher(camelCase).replaceAll("$1_$2").toLowerCase();
	}

	/**
	 * Capitalize the first letter of every word in a string.
	 */
	public static String title(String s) {
		if (s == null) {
			return null;
		}
		if (s.length() == 0) {
			return s;
		}
		StringBuilder sb = new StringBuilder();
		Arrays.stream(s.split("\\s+")).forEach(part -> sb.append(Character.toTitleCase(part.charAt(0))).append(part.substring(1)).append(" "));
		return sb.toString().trim();
	}

	/**
	 * Returns an HTML-escaped version of the string.
	 */
	public static String escapeHtml(String s) {
		return StringEscapeUtils.escapeHtml4(s);
	}

	/**
	 * Private constructor so that no instance is created
	 */
	private StringUtilities() {
		throw new IllegalStateException("Cannot create an instance of static class Util");
	}
}