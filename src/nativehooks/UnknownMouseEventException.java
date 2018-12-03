package nativehooks;

@SuppressWarnings("serial")
public class UnknownMouseEventException extends IllegalStateException {
	private final String error;

	public UnknownMouseEventException(String error) {
		this.error = error;
	}

	public String getError() {
		return error;
	}
}
