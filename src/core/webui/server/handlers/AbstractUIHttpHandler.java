package core.webui.server.handlers;

import core.webui.server.handlers.renderedobjects.ObjectRenderer;

public abstract class AbstractUIHttpHandler extends AbstractSingleMethodHttpHandler {
	protected ObjectRenderer objectRenderer;

	public AbstractUIHttpHandler(ObjectRenderer objectRenderer, String allowedMethod) {
		super(allowedMethod);
		this.objectRenderer = objectRenderer;
	}
}
