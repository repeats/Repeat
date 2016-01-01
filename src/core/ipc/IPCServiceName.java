package core.ipc;

public enum IPCServiceName {
	CONTROLLER_SERVER(0),
    PYTHON(1),
    CSHARP(2),
    ;

	public static IPCServiceName[] ALL_SERVICE_NAMES = new IPCServiceName[] {CONTROLLER_SERVER, PYTHON, CSHARP};
    private final int index;

    /**
     * @param index
     */
    private IPCServiceName(final int index) {
        this.index = index;
    }

    protected int value() {
    	return index;
    }

    @Override
    public String toString() {
        return index + "";
    }
}
