package core.languageHandler;

public enum Languages {
	JAVA("java"),
    PYTHON("python")
    ;

    private final String text;

    /**
     * @param text
     */
    private Languages(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
