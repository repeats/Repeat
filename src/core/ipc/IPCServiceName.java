package core.ipc;

import core.languageHandler.Language;

public enum IPCServiceName {
	CONTROLLER_SERVER(0, "controller_server"),
	CLI_SERVER(1, "cli_server"),
    PYTHON(2, Language.PYTHON.toString()),
    CSHARP(3, Language.CSHARP.toString()),
    SCALA(4, Language.SCALA.toString()),
    ;

    private final int index;
    private final String name;

    /**
     * @param index
     */
    private IPCServiceName(final int index, final String name) {
        this.index = index;
        this.name = name;
    }

    protected int value() {
    	return index;
    }

    protected static IPCServiceName identifyService(String name) {
    	for (IPCServiceName service : IPCServiceName.values()) {
    		if (name.equals(service.name)) {
    			return service;
    		}
    	}
    	return null;
    }

    @Override
    public String toString() {
        return name;
    }
}
