package utilities;

public class OSIdentifier {

	public static final String OS_NAME;
	public static final boolean IS_WINDOWS, IS_UNIX;

	static {
		OS_NAME = System.getProperty("os.name");
		IS_WINDOWS = OS_NAME.startsWith("Windows");
		IS_UNIX = !IS_WINDOWS;
	}

	private OSIdentifier() {}
}
