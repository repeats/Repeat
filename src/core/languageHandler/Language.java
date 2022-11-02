package core.languageHandler;

public enum Language {
	JAVA("java"),
    PYTHON("python"),
    CSHARP("C#"),
    MANUAL_BUILD("manual"),
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

    public static Language identify(int index) {
    	Language[] languages = Language.values();
    	if (index < 0 || index >= languages.length) {
    		return null;
    	}
    	return languages[index];
    }

    public static Language identify(String name) {
    	for (Language language : Language.values()) {
    		if (name.equals(language.toString())) {
    			return language;
    		}
    	}
    	return null;
    }
}
