package staticResources;

import java.util.logging.Level;
import java.util.logging.Logger;

import utilities.NumberUtility;

/**
 * Handle the loading and parsing of mouse gesture recognition model.
 */
public class MouseGestureModelResources {

	private static final Logger LOGGER = Logger.getLogger(MouseGestureModelResources.class.getName());

	private static final String INTERCEPTS_FILE = "/mouseGestureModel/intercepts";
	private static final String COEFFICIENTS_FILE = "/mouseGestureModel/coefficients";
	private static final String LABELS_FILE = "/mouseGestureModel/labels";

	/**
	 * Load the intercepts from file. Each line is an intercept in IEEE 754 single precision form.
	 *
	 * @return array of intercepts.
	 */
	public static double[] getIntercepts() {
		String content = BootStrapResources.getFile(INTERCEPTS_FILE);
		String[] lines = content.split("\n");

		double[] output = new double[lines.length];
		for (int i = 0; i < lines.length; i++) {
			try {
				float value = NumberUtility.fromIEEE754Binary(lines[i]);
				output[i] = value;
			} catch (NumberFormatException e) {
				LOGGER.log(Level.WARNING, e.getMessage(), e);
				return null;
			}
		}

		return output;
	}

	/**
	 * Load the coefficients matrix from file. Each line is an intercept in IEEE 754 single precision form.
	 * Note that since the feature space and classes space are small, it is OK to store matrix in 2D array
	 * without worrying about performance issue.
	 *
	 *
	 * @param labelCount number of label, which is the same as number of row for this matrix.
	 * @return the coefficients matrix.
	 */
	public static double[][] getCoefficients(int labelCount) {
		String content = BootStrapResources.getFile(COEFFICIENTS_FILE);
		String[] lines = content.split("\n");

		int colCount = lines.length / labelCount;
		double[][] output = new double[labelCount][colCount];

		int row = 0, col = 0;
		for (String line : lines) {
			try {
				float value = NumberUtility.fromIEEE754Binary(line);
				output[row][col++] = value;
			} catch (NumberFormatException e) {
				LOGGER.log(Level.WARNING, e.getMessage(), e);
				return null;
			}

			if (col == colCount) {
				col = 0;
				row++;
			}
		}

		return output;
	}

	/**
	 * Load the list of labels from file. Each line is a label name.
	 *
	 * @return list of labels loaded.
	 */
	public static String[] getLabels() {
		String content = BootStrapResources.getFile(LABELS_FILE);
		if (content == null) {
			return null;
		}

		return content.split("\n");
	}
}
