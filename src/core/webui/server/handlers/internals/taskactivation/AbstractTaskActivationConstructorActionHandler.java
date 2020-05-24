package core.webui.server.handlers.internals.taskactivation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.keyChain.TaskActivationConstructor;
import core.keyChain.TaskActivationConstructorManager;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.server.handlers.renderedobjects.RenderedDetailedUserDefinedAction;
import core.webui.webcommon.HttpServerUtilities;

public abstract class AbstractTaskActivationConstructorActionHandler extends AbstractUIHttpHandler {

	protected TaskActivationConstructorManager taskActivationConstructorManager;

	public AbstractTaskActivationConstructorActionHandler(ObjectRenderer objectRenderer, TaskActivationConstructorManager taskActivationConstructorManager) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.POST_METHOD);
		this.taskActivationConstructorManager = taskActivationConstructorManager;
	}

	@Override
	protected final Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
			throws HttpException, IOException {
		Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(request);
		if (params == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Failed to get POST parameters.");
		}

		String id = params.get("id");
		if (id == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Failed to get task activation constructor ID.");
		}

		TaskActivationConstructor constructor = taskActivationConstructorManager.get(id);
		if (constructor == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 404, "No constructor found for ID '" + id + "'.");
		}

		return handleRequestWithBackendAndConstructor(exchange, constructor, params);
	}

	protected final Void renderedTaskActivationPage(HttpAsyncExchange exchange, String template, TaskActivationConstructor constructor) throws IOException {
		Map<String, Object> data = new HashMap<>();
		data.put("task", RenderedDetailedUserDefinedAction.withEmptyTaskInfo(constructor));
		return renderedPage(exchange, template, data);
	}

	protected abstract Void handleRequestWithBackendAndConstructor(HttpAsyncExchange exchange, TaskActivationConstructor constructor, Map<String, String> params) throws HttpException, IOException;
}
