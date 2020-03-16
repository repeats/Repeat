package core.webui.server.handlers.internals.tasks;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.keyChain.TaskActivationConstructor;
import core.keyChain.TaskActivationConstructorManager;
import core.userDefinedTask.UserDefinedAction;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.server.handlers.renderedobjects.RenderedDetailedUserDefinedAction;
import core.webui.webcommon.HttpServerUtilities;

public class TaskDetailsPageHandler extends AbstractUIHttpHandler {

	private TaskActivationConstructorManager taskActivationConstructorManager;

	public TaskDetailsPageHandler(ObjectRenderer objectRenderer, TaskActivationConstructorManager taskActivationConstructorManager) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.GET_METHOD);
		this.taskActivationConstructorManager = taskActivationConstructorManager;
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
		String uriString = request.getRequestLine().getUri();
		Map<String, String>  params = HttpServerUtilities.parseGetParameters(uriString);
		if (params == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Failed to parse URL " + uriString);
		}

		String id = params.get("id");
		if (id == null || id.isEmpty()) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Task ID is empty or not provided.");
		}

		UserDefinedAction action = backEndHolder.getTask(id);
		if (action == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 404, "Cannot find task with ID " + id + ".");
		}

		Map<String, Object> data = new HashMap<>();
		String activationConstructorId = taskActivationConstructorManager.addNew(action.getActivation());
		TaskActivationConstructor activationConstructor = taskActivationConstructorManager.getConstructor(activationConstructorId);
		RenderedDetailedUserDefinedAction renderedDetailedUserDefinedAction = RenderedDetailedUserDefinedAction.fromUserDefinedAction(action, activationConstructor);
		data.put("task", renderedDetailedUserDefinedAction);
		data.put("taskActivationConstructorId", activationConstructorId);
		data.put("activation", renderedDetailedUserDefinedAction.getActivation());
		return renderedPage(exchange, "task_details", data);
	}

}
