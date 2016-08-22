package core.keyChain.mouseGestureRecognition;

import java.awt.Point;
import java.util.ArrayList;

public class DataNormalizer {

	public static final int POINT_COUNT = 35;
	public static final int FEATURE_COUNT = POINT_COUNT * 2;

	/**
	 * Trim down the list of points to have exactly {@link POINT_COUNT}.
	 * Then scale the points so that they fit into a unit square.
	 *
	 * @param input input list of points to normalize.
	 * @return list of normalized points flattenned into (x1,y1,x2,y2,...).
	 */
	public ArrayList<Float> normalize(ArrayList<Point> input) {
		input = new DataDefinitionTrimmer().trim(input);

		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

		for (Point p : input) {
			minX = Math.min(minX, p.x);
			minY = Math.min(minY, p.y);

			maxX = Math.max(maxX, p.x);
			maxY = Math.max(maxY, p.y);
		}

		int width = maxX - minX;
		width = width == 0 ? 1 : width;

		int height = maxY - minY;
		height = height == 0 ? 1 : height;

		float midX, midY, subX, subY;

		// Fit this into a square box. Center the appropriate dimension.
		if (height > width) {
			midX = (maxX + minX) / 2;
			subX = midX - height / 2;
			subY = minY;
		} else if (width > height) {
			subX = minX;
			midY = (maxY + minY) / 2;
			subY = midY - width / 2;
		} else {
			subX = minX;
			subY = minY;
		}

		ArrayList<Point.Float> centered = new ArrayList<>(input.size());
		for (int i = 0; i < input.size(); i++) {
			Point current = input.get(i);
			centered.add(new Point.Float(current.x - subX, current.y - subY));
		}

		ArrayList<Float> output = new ArrayList<>(input.size() * 2);
		// Now make it into a unit square and flatten
		float scale = Math.max(width, height);
		for (int i = 0; i < centered.size(); i++) {
			Point.Float current = centered.get(i);
			output.add(current.x / scale);
			output.add(current.y / scale);
		}

		return output;
	}
}
