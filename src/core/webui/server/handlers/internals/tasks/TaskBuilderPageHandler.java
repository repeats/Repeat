package core.webui.server.handlers.internals.tasks;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.userDefinedTask.manualBuild.ManuallyBuildActionConstructorManager;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.internals.tasks.manuallybuild.ManuallyBuildActionBuilderBody;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;

public class TaskBuilderPageHandler extends AbstractUIHttpHandler {

	private ManuallyBuildActionConstructorManager manuallyBuildActionConstructorManager;

	public TaskBuilderPageHandler(ObjectRenderer objectRenderer, ManuallyBuildActionConstructorManager manuallyBuildActionConstructorManager) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.GET_METHOD);
		this.manuallyBuildActionConstructorManager = manuallyBuildActionConstructorManager;
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
			throws HttpException, IOException {
		String id = manuallyBuildActionConstructorManager.addNew();
		Map<String, Object> data = ManuallyBuildActionBuilderBody.bodyData(manuallyBuildActionConstructorManager, id);
		return renderedPage(exchange, "task_builder", data);
	}
}
