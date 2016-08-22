package core.keyChain.mouseGestureRecognition;

import java.awt.Point;
import java.util.ArrayList;
import java.util.ListIterator;

public class DataCutTrimmer extends AbstractDataTrimmer {

	/**
	 * Remove points from the beginning of the list.
	 */
	@Override
	public ArrayList<Point> internalTrim(ArrayList<Point> input) {
		if (input.size() <= DataNormalizer.POINT_COUNT) {
			return input;
		}

		ArrayList<Point> output = new ArrayList<>(DataNormalizer.POINT_COUNT);
		ListIterator<Point> it = input.listIterator(input.size() - DataNormalizer.POINT_COUNT);
		while (it.hasNext()) {
			output.add(it.next());
		}

		return output;
	}

}
