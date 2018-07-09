package core.webui.server.handlers;

import core.webcommon.HttpHandlerWithBackend;
import core.webui.server.ResourceManager;

public abstract class AbstractUIHttpHandler extends HttpHandlerWithBackend {
	protected ResourceManager resourceManager;

	public AbstractUIHttpHandler(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}
}
