package core.keyChain;

/**
 * Enum representing classification categories
 *
 */
public enum MouseGesture {
	ALPHA("alpha"),
	DERIVATIVE("derivative"),
	GREATER_THAN("greater_than"),
	HAT("hat"),
	HORIZONTAL("horizontal"),
	RANDOM("random"),
	TRIANGLE("triangle"),
	VERTICAL("vertical")
	;


	private final String text;

    /**
     * @param text human readable text form of this classification
     */
    private MouseGesture(final String text) {
        this.text = text;
    }

    protected static MouseGesture find(String name) {
    	for (MouseGesture classification : MouseGesture.values()) {
    		if (classification.text.equals(name)) {
    			return classification;
    		}
    	}

    	return null;
    }
}