package utilities;

import java.util.List;
import java.util.ListIterator;

public class IterableUtility {

	public static int[] toIntegerArray(List<Integer> list) {
		int[] output = new int[list.size()];

		for (ListIterator<Integer> iterator = list.listIterator(); iterator.hasNext();) {
			int i = iterator.nextIndex();
			Object o = iterator.next();
			output[i] = (int) o;
		}
		return output;
	}

	/**
	 * Private constructor so that no instance is created
	 */
	private IterableUtility() {};
}
