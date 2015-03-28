package utilities;

import java.awt.Color;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Provide static interface to utilities
 *
 * @author HP
 *
 */
public class StringUtilities {

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
	 * Private constructor so that no instance is created
	 */
	private StringUtilities() {
		throw new IllegalStateException("Cannot create an instance of static class Util");
	}
}