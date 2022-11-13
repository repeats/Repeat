package core.webui.server.handlers.internals.tasks;

import java.io.IOException;
import java.util.Set;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import argo.jdom.JsonNode;
import core.keyChain.KeyChain;
import core.keyChain.TaskActivation;
import core.keyChain.TaskActivationConstructor;
import core.keyChain.TaskActivationConstructorManager;
import core.userDefinedTask.UserDefinedAction;
import core.userDefinedTask.internals.preconditions.ActiveWindowsInfoCondition;
import core.userDefinedTask.internals.preconditions.AlwaysMatchingStringCondition;
import core.userDefinedTask.internals.preconditions.RegexStringMatchingCondition;
import core.userDefinedTask.internals.preconditions.StringMatchingCondition;
import core.userDefinedTask.internals.preconditions.TaskExecutionPreconditions;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.CommonTask;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;

public class ActionSaveTaskDetailsHandler extends AbstractUIHttpHandler {

	protected TaskActivationConstructorManager taskActivationConstructorManager;

	public ActionSaveTaskDetailsHandler(ObjectRenderer objectRenderer,
			TaskActivationConstructorManager taskActivationConstructorManager) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.POST_METHOD);
		this.taskActivationConstructorManager = taskActivationConstructorManager;
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
			throws HttpException, IOException {
		JsonNode params = HttpServerUtilities.parsePostParameters(request);
		if (params == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Failed to get POST parameters.");
		}
		if (!validateInput(exchange, params)) {
			return null;
		}

		String id = params.getStringValue("id");
		TaskActivationConstructor constructor = taskActivationConstructorManager.get(id);
		if (constructor == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 404, "No constructor found for ID '" + id + "'.");
		}

		String taskString = params.getStringValue("task");
		if (isHotkey(taskString)) {
			return handleSaveHotkey(exchange, constructor.getActivation(), taskString);
		}

		UserDefinedAction task = CommonTask.getTaskFromId(backEndHolder, taskString);
		if (task == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Cannot get task from request.");
		}

		TaskActivation activation = constructor.getActivation();
		if (!backEndHolder.changeHotkeyTask(task, activation)) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Cannot change task activation.");
		}
		TaskExecutionPreconditions preconditions = getTaskExecutionPreconditions(params);
		task.setExecutionPreconditions(preconditions);

		taskActivationConstructorManager.remove(id);
		return HttpServerUtilities.prepareHttpResponse(exchange, 200, "");
	}

	private boolean validateInput(HttpAsyncExchange exchange, JsonNode params) throws IOException {
		if (!params.isStringValue("id")) {
			HttpServerUtilities.prepareHttpResponse(exchange, 400, "Failed to get task activation constructor ID.");
			return false;
		}

		if (!params.isStringValue("task")) {
			HttpServerUtilities.prepareHttpResponse(exchange, 400, "No task ID provided.");
			return false;
		}

		if (!params.isNode("preconditions")) {
			return true;
		}

		JsonNode preconditions = params.getNode("preconditions");
		if (!preconditions.isStringValue("activeWindowTitle")) {
			HttpServerUtilities.prepareHttpResponse(exchange, 400, "No active window title precondition provided.");
			return false;
		}
		if (!preconditions.isStringValue("activeProcessName")) {
			HttpServerUtilities.prepareHttpResponse(exchange, 400, "No active process name precondition provided.");
			return false;
		}

		return true;
	}

	private Void handleSaveHotkey(HttpAsyncExchange exchange, TaskActivation activation, String taskString) throws IOException {
		Set<KeyChain> hotKeys = activation.getHotkeys();
		if (hotKeys.isEmpty()) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "There is no hot key to set!");
		}
		KeyChain hotKey = hotKeys.iterator().next();

		if (taskString.equals("record")) {
			backEndHolder.getConfig().setRECORD(hotKey);
			backEndHolder.reconfigureSwitchRecord();
			return emptySuccessResponse(exchange);
		}
		if (taskString.equals("replay")) {
			backEndHolder.getConfig().setREPLAY(hotKey);
			backEndHolder.reconfigureSwitchReplay();
			return emptySuccessResponse(exchange);
		}
		if (taskString.equals("runCompiled")) {
			backEndHolder.getConfig().setCOMPILED_REPLAY(hotKey);
			backEndHolder.reconfigureSwitchCompiledReplay();
			return emptySuccessResponse(exchange);
		}
		if (taskString.equals("mouseGestureActivation")) {
			backEndHolder.getConfig().setMouseGestureActivationKey(hotKey.getButtonStrokes().iterator().next().getKey());
			return emptySuccessResponse(exchange);
		}
		return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Unknown hotkey " + taskString + ".");
	}

	private boolean isHotkey(String taskString) {
		return taskString != null &&
				(TaskDetailsPageHandler.HOTKEY_NAMES.containsKey(taskString));
	}

	private TaskExecutionPreconditions getTaskExecutionPreconditions(JsonNode params) {
		String activeWindowTitle = params.getNode("preconditions").getStringValue("activeWindowTitle");
		String activeProcessName = params.getNode("preconditions").getStringValue("activeProcessName");

		StringMatchingCondition titleCondition = activeWindowTitle.isEmpty() ? AlwaysMatchingStringCondition.INSTANCE : RegexStringMatchingCondition.of(activeWindowTitle);
		StringMatchingCondition processNameCondition = activeProcessName.isEmpty() ? AlwaysMatchingStringCondition.INSTANCE : RegexStringMatchingCondition.of(activeProcessName);
		ActiveWindowsInfoCondition windowsInfoCondition = ActiveWindowsInfoCondition.of(titleCondition, processNameCondition);
		return TaskExecutionPreconditions.of(windowsInfoCondition);
	}
}
