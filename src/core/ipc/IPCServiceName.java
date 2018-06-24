package core.ipc;

import core.languageHandler.Language;

public enum IPCServiceName {
	CONTROLLER_SERVER(0, "controller_service"),
    PYTHON(1, Language.PYTHON.toString()),
    CSHARP(2, Language.CSHARP.toString()),
    SCALA(3, Language.SCALA.toString()),
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
