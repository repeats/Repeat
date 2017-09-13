package utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FuzzySearch {

	public static interface FuzzySearchable {
		public String getString();
	}
	
	/**
	 * Provided a key, filter and reorder a list of options using the two steps:
	 * 1) Remove all strings that do not have characters in the key appearing in the same order.
	 * 2) Sort the rest using edit distance.
	 * 
	 * @param options list of option strings
	 * @param key key string to search
	 * @return filtered and sorted list of results.
	 */
	public static List<FuzzySearchable> fuzzySearch(List<FuzzySearchable> options, String key) {
		List<FuzzySearchable> output = new ArrayList<>();
		
		// First eliminate options that do not have all characters in key string
		for (FuzzySearchable searchable : options) {
			String s = searchable.getString();
			if (hasAllCharsInString(s, key)) {
				output.add(searchable);
			}
		}
		
		// Sort by edit distance
		Collections.sort(output, new Comparator<FuzzySearchable>() {
			@Override
			public int compare(FuzzySearchable fs0, FuzzySearchable fs1) {
				String s0 = fs0.getString();
				String s1 = fs1.getString();
				
				int d0 = StringUtilities.levenshteinDistance(s0, key);
				int d1 = StringUtilities.levenshteinDistance(s1, key);
				return d0 - d1;
			}
		});
		
		return output;
	}
	
	/**
	 * Check if all characters in a smaller string are present in a larger string
	 * in the same order that they appear in the smaller string.
	 * 
	 * @param reference the larger string
	 * @param subString the smaller string
	 * @return whether all characters in the smaller string appear in the larger string
	 * in the same order.
	 */
	private static boolean hasAllCharsInString(String reference, String subString) {
		int i = 0, j = 0;
		
		while (i < reference.length() && j < subString.length()) {
			char cr = reference.charAt(i);
			char cs = subString.charAt(j);
			
			if (cr == cs) {
				j++;
			}
			i++;
		}
		
		return j == subString.length();
	}
	
	private FuzzySearch() {}
}
