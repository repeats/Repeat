package core.keyChain.mouseGestureRecognition;

import java.awt.Point;
import java.util.ArrayList;

public class DataDefinitionTrimmer extends AbstractDataTrimmer {

	/**
	 * Leaving out points once every period.
	 */
	@Override
	protected ArrayList<Point> internalTrim(ArrayList<Point> input) {
		int length = input.size();
		float leaveOutPeriod = (float) length / (length - DataNormalizer.POINT_COUNT);
		ArrayList<Point> result = new ArrayList<>(DataNormalizer.POINT_COUNT);
		int previousBase = -1;

		for (int i = 0; i < length; i++) {
			if (i == 0) {
				previousBase = 0;
				result.add(input.get(0));
				continue;
			}

			int newBase = (int) ((i + 1) / leaveOutPeriod);
			if (newBase == previousBase) {
				result.add(input.get(i));
			}

			previousBase = newBase;
		}

		return new DataCutTrimmer().trim(result);
	}
}
