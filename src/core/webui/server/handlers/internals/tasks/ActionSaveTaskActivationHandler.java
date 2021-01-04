package core.webui.server.handlers.internals.tasks;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpException;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import core.keyChain.KeyChain;
import core.keyChain.TaskActivation;
import core.keyChain.TaskActivationConstructor;
import core.keyChain.TaskActivationConstructorManager;
import core.userDefinedTask.UserDefinedAction;
import core.webui.server.handlers.CommonTask;
import core.webui.server.handlers.internals.taskactivation.AbstractTaskActivationConstructorActionHandler;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;

public class ActionSaveTaskActivationHandler extends AbstractTaskActivationConstructorActionHandler {

	public ActionSaveTaskActivationHandler(ObjectRenderer objectRenderer,
			TaskActivationConstructorManager taskActivationConstructorManager) {
		super(objectRenderer, taskActivationConstructorManager);
	}

	@Override
	protected Void handleRequestWithBackendAndConstructor(HttpAsyncExchange exchange,
			TaskActivationConstructor constructor, Map<String, String> params) throws HttpException, IOException {
		String taskString = params.get("task");
		if (isHotkey(taskString)) {
			return handleSaveHotkey(exchange, constructor.getActivation(), taskString);
		}

		UserDefinedAction task = CommonTask.getTaskFromRequest(backEndHolder, params);
		if (task == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Cannot get task from request.");
		}

		TaskActivation activation = constructor.getActivation();
		backEndHolder.changeHotkeyTask(task, activation);

		taskActivationConstructorManager.remove(params.get("id"));
		return HttpServerUtilities.prepareHttpResponse(exchange, 200, "");
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
				(taskString.equals("record")
				|| taskString.equals("replay")
				|| taskString.equals("runCompiled")
				|| taskString.equals("mouseGestureActivation"));
	}
}
