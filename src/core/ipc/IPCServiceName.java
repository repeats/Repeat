package core.ipc;

import core.languageHandler.Language;

public enum IPCServiceName {
	CONTROLLER_SERVER(0, "controller_server"),
	CLI_SERVER(1, "cli_server"),
	WEB_UI_SERVER(2, "web_ui_server"),
    PYTHON(3, Language.PYTHON.toString()),
    CSHARP(4, Language.CSHARP.toString()),
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
