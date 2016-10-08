package core.languageHandler.sourceGenerator;

public abstract class AbstractDeviceSourceGenerator {

	/**
	 * Generate source code for an action.
	 *
	 * @param action action to generate source code for.
	 * @param params parameters for this action.
	 * @return the source code for this action, or null if error occurs.
	 */
	protected String getSourceCode(String action, int[] params) {
		if (!isKnownAction(action)) {
			return null;
		}

		return internalGetSourceCode(action, params);
	}

	/**
	 * Generate source code for a known (i.e. recognized/accepted) action.
	 *
	 * @param action action to generate source code for.
	 * @param param parameters for this action.
	 * @return the source code for this action, or null if error occurs.
	 */
	protected abstract String internalGetSourceCode(String action, int[] param);

	/**
	 * @param action action to check.
	 * @return if the action is known (i.e. recognized/accepted).
	 */
	protected abstract boolean isKnownAction(String action);
}
