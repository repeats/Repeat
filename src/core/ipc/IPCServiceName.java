package core.ipc;

public enum IPCServiceName {
	CONTROLLER_SERVER(0),
    PYTHON(1),
    CSHARP(2),
    SCALA(3),
    ;

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
