package core.keyChain.mouseGestureRecognition;


import staticResources.MouseGestureModelResources;
import Jama.Matrix;

/**
 * Logistic regression model raw implementation. This only implements the predict function.
 *
 * @author HP Truong
 */
class LogisticRegressionModel {

	private String[] labels; // classifying classes
	private Matrix w; // coefficients
	private Matrix c; // intercepts

	/**
	 * Load model data from static resources.
	 *
	 * @return if operation was successful.
	 */
	protected boolean load() {
		labels = MouseGestureModelResources.getLabels();
		if (labels == null) {
			return false;
		}

		double[] intercepts = MouseGestureModelResources.getIntercepts();
		double[][] coefficients = MouseGestureModelResources.getCoefficients(labels.length);

		w = new Matrix(coefficients);

		double[][] twoDimensionalIntercept = new double[1][intercepts.length];
		twoDimensionalIntercept[0] = intercepts;
		c = new Matrix(twoDimensionalIntercept);

		return isLoaded();
	}

	/**
	 * Predict using logistic regression and the loaded values for the model.
	 * Specifically, this performs the matrix operation:
	 * 		prediction = x * {@link #w}.T + {@link #c}
	 * 		where {@link w}.T is the transpose of {@link w}
	 * and select the label with highest prediction value.
	 *
	 * @param x the values of the features.
	 * @return predicted label, or null if problem occurs.
	 */
	protected String predict(double[] featureValues) {
		if (!isLoaded()) {
			return null;
		}

		double[][] values = new double[1][featureValues.length];
		values[0] = featureValues;
		Matrix x = new Matrix(values);
		Matrix result = x.times(w.transpose()).plus(c);

		double[] probabilities = result.getArray()[0];
		int maxIndex = -1;
		double maxValue = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < probabilities.length; i++) {
			double p = probabilities[i];
			if (p > maxValue) {
				maxValue = p;
				maxIndex = i;
			}
		}

		return labels[maxIndex];
	}

	/**
	 * @return whether model was loaded successfully.
	 */
	protected boolean isLoaded() {
		return labels != null && w != null && c != null;
	}
}
