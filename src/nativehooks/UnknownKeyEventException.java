package nativehooks;

@SuppressWarnings("serial")
public class UnknownKeyEventException extends IllegalStateException {
	private final String error;

	public UnknownKeyEventException(String error) {
		this.error = error;
	}

	public String getError() {
		return error;
	}
}
