package cli;

public enum CliExitCodes {
	// Exit codes 1 - 2, 126 - 165, and 255 are reserved.
	INVALID_ARGUMENTS(3),
	UNKNOWN_MODULE(4),
	UNKNOWN_ACTION(5);

	private int code;

	private CliExitCodes(int code) {
		this.code = code;
	}

	public void exit() {
		System.exit(code);
	}

	public int getCode() {
		return code;
	}
}
