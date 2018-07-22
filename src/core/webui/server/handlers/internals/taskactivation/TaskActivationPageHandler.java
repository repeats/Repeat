package core.webui.server.handlers.internals.taskactivation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.keyChain.TaskActivation;
import core.keyChain.TaskActivationConstructor;
import core.keyChain.TaskActivationConstructorManager;
import core.userDefinedTask.TaskGroup;
import core.userDefinedTask.UserDefinedAction;
import core.webcommon.HttpServerUtilities;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.CommonTask;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.server.handlers.renderedobjects.RenderedActivation;

public class TaskActivationPageHandler extends AbstractUIHttpHandler {

	private TaskActivationConstructorManager taskActivationConstructorManager;

	public TaskActivationPageHandler(ObjectRenderer objectRenderer, TaskActivationConstructorManager taskActivationConstructorManager) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.GET_METHOD);
		this.taskActivationConstructorManager = taskActivationConstructorManager;
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
			throws HttpException, IOException {
		String uriString = request.getRequestLine().getUri();
		Map<String, String>  params = HttpServerUtilities.parseGetParameters(uriString);
		if (params == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Failed to parse URL " + uriString);
		}

		TaskGroup group = backEndHolder.getCurrentTaskGroup();
		int taskGroupIndex = CommonTask.getTaskGroupIndexFromRequest(backEndHolder, params);
		if (taskGroupIndex != -1) {
			group = backEndHolder.getTaskGroup(taskGroupIndex);
		} else {
			taskGroupIndex = backEndHolder.getCurentTaskGroupIndex();
		}
		int taskIndex = CommonTask.getTaskIndexFromRequest(backEndHolder, params, group);
		if (taskIndex == -1) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Cannot get task index.");
		}

		UserDefinedAction task = CommonTask.getTaskFromRequest(backEndHolder, params);
		if (task == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Failed to get task.");
		}

		TaskActivation activation = task.getActivation();
		String id = params.get("id");

		if (id == null) {
			id = taskActivationConstructorManager.addNew(activation);
			Map<String, String> redirectData = new HashMap<>();
			redirectData.put("group", taskGroupIndex + "");
			redirectData.put("task", taskIndex + "");
			redirectData.put("id", id);
			return HttpServerUtilities.redirect(exchange, "/task-activation", redirectData);
		} else {
			TaskActivationConstructor constructor = taskActivationConstructorManager.getConstructor(id);
			if (constructor == null) {
				return HttpServerUtilities.prepareHttpResponse(exchange, 400, "No task activation constructor with id = " + id);
			}
			constructor.clearStrokes();
			activation = constructor.getActivation();
		}

		Map<String, Object> data = new HashMap<>();
		data.put("groupIndex", taskGroupIndex);
		data.put("taskIndex", taskIndex);
		data.put("activation", RenderedActivation.fromActivation(activation));
		data.put("taskActivationConstructorId", id);

		return renderedPage(exchange, "task_activation", data);
	}
}
