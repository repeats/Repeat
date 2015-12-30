package core.languageHandler;

public enum Language {
	JAVA("java"),
    PYTHON("python"),
    CSHARP("C#"),
    ;

	public static Language[] ALL_LANGUAGES = {JAVA, PYTHON, CSHARP};
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
    	for (Language language : ALL_LANGUAGES) {
    		if (name.equals(language.toString())) {
    			return language;
    		}
    	}
    	return null;
    }
}
