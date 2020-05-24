package core.webui.server.handlers.internals.tasks.manuallybuild;

public class InvalidSuppliedBuildStepException extends Exception {
	private static final long serialVersionUID = 5763742112199815619L;

	private String message;

	public InvalidSuppliedBuildStepException(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return message;
	}
}