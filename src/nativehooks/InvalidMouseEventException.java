package nativehooks;

@SuppressWarnings("serial")
public class InvalidMouseEventException extends Exception {
	private final String error;

	public InvalidMouseEventException(String error) {
		this.error = error;
	}

	public String getError() {
		return error;
	}
}
