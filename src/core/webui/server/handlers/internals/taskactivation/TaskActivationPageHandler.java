package core.webui.server.handlers.internals.taskactivation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.keyChain.KeyChain;
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
import core.webui.server.handlers.renderedobjects.RenderedTaskActivation;

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

		String id = params.get("id");
		String taskString = params.get("task");
		if (taskString == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Task must be provided.");
		}

		int taskIndex = -1;
		int taskGroupIndex = -1;
		boolean isHotkey = isHotkey(taskString);
		if (!isHotkey) {
			TaskGroup group = backEndHolder.getCurrentTaskGroup();
			taskGroupIndex = CommonTask.getTaskGroupIndexFromRequest(backEndHolder, params);
			if (taskGroupIndex != -1) {
				group = backEndHolder.getTaskGroup(taskGroupIndex);
			} else {
				taskGroupIndex = backEndHolder.getCurentTaskGroupIndex();
			}
			taskIndex = CommonTask.getTaskIndexFromRequest(backEndHolder, params, group);
			if (taskIndex == -1) {
				return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Cannot get task index.");
			}
		}

		if (id == null) {
			Map<String, String> redirectData = new HashMap<>();
			if (isHotkey) {
				return handleNewHotkey(exchange, taskString);
			}

			UserDefinedAction task = CommonTask.getTaskFromRequest(backEndHolder, params);
			if (task == null) {
				return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Failed to get task.");
			}
			id = taskActivationConstructorManager.addNew(task.getActivation());
			redirectData.put("group", taskGroupIndex + "");
			redirectData.put("task", taskIndex + "");
			redirectData.put("id", id);
			return HttpServerUtilities.redirect(exchange, "/task-activation", redirectData);
		}

		TaskActivationConstructor constructor = taskActivationConstructorManager.getConstructor(id);
		if (constructor == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "No task activation constructor with id = " + id);
		}
		constructor.clearStrokes();

		Map<String, Object> data = new HashMap<>();
		data.put("groupIndex", taskGroupIndex);
		data.put("taskIndex", taskString);
		data.put("activation", RenderedTaskActivation.fromActivation(constructor));
		data.put("taskActivationConstructorId", id);

		return renderedPage(exchange, "task_activation", data);
	}

	private Void handleNewHotkey(HttpAsyncExchange exchange, String taskString) throws IOException {
		Map<String, String> redirectData = new HashMap<>();
		redirectData.put("task", taskString);
		if (taskString.equals("record")) {
			KeyChain recordKeyChain = backEndHolder.getConfig().getRECORD();
			String id = taskActivationConstructorManager.addNew(TaskActivation.newBuilder().withHotKey(recordKeyChain).build(), TaskActivationConstructor.Config.ofRestricted().setDisableKeyChain(false));
			redirectData.put("id", id);
			return HttpServerUtilities.redirect(exchange, "/task-activation", redirectData);
		}
		if (taskString.equals("replay")) {
			KeyChain replayKeyChain = backEndHolder.getConfig().getREPLAY();
			String id = taskActivationConstructorManager.addNew(TaskActivation.newBuilder().withHotKey(replayKeyChain).build(), TaskActivationConstructor.Config.ofRestricted().setDisableKeyChain(false));
			redirectData.put("id", id);
			return HttpServerUtilities.redirect(exchange, "/task-activation", redirectData);
		}
		if (taskString.equals("runCompiled")) {
			KeyChain runCompiledKeyChain = backEndHolder.getConfig().getCOMPILED_REPLAY();
			String id = taskActivationConstructorManager.addNew(TaskActivation.newBuilder().withHotKey(runCompiledKeyChain).build(), TaskActivationConstructor.Config.ofRestricted().setDisableKeyChain(false));
			redirectData.put("id", id);
			return HttpServerUtilities.redirect(exchange, "/task-activation", redirectData);
		}
		if (taskString.equals("mouseGestureActivation")) {
			KeyChain mouseGestureKeyChain = new KeyChain(backEndHolder.getConfig().getMouseGestureActivationKey());
			String id = taskActivationConstructorManager.addNew(TaskActivation.newBuilder().withHotKey(mouseGestureKeyChain).build(), TaskActivationConstructor.Config.ofRestricted().setDisableKeyChain(false).setMaxStrokes(1));
			redirectData.put("id", id);
			return HttpServerUtilities.redirect(exchange, "/task-activation", redirectData);
		}

		return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Unknown hotkey.");
	}

	private boolean isHotkey(String taskString) {
		return taskString.equals("record")
				|| taskString.equals("replay")
				|| taskString.equals("runCompiled")
				|| taskString.equals("mouseGestureActivation");
	}
}
