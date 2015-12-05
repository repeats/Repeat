package core.languageHandler;

public enum Language {
	JAVA("java"),
    PYTHON("python")
    ;

    private final String text;

    /**
     * @param text
     */
    private Language(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static Language identify(String name) {
    	if (name.equals(JAVA.toString())) {
    		return JAVA;
    	} else if (name.equals(PYTHON.toString())) {
    		return PYTHON;
    	} else {
    		return null;
    	}
    }
}
