package core.webui.server.handlers.internals.tasks;

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
import core.userDefinedTask.UserDefinedAction;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.server.handlers.renderedobjects.RenderedDetailedUserDefinedAction;
import core.webui.webcommon.HttpServerUtilities;

public class TaskDetailsPageHandler extends AbstractUIHttpHandler {

	private static final Map<String, String> HOTKEY_NAMES;

	static {
		HOTKEY_NAMES = new HashMap<>();
		HOTKEY_NAMES.put("record", "Start/Stop recording");
		HOTKEY_NAMES.put("replay", "Start/Stop replaying");
		HOTKEY_NAMES.put("runCompiled", "Run compiled task");
		HOTKEY_NAMES.put("mouseGestureActivation", "Mouse gesture recognition activation/de-activation");
	}

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
		if (isHotkey(id)) {
			return handleNewHotkey(exchange, id);
		}

		UserDefinedAction action = backEndHolder.getTask(id);
		if (action == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 404, "Cannot find task with ID " + id + ".");
		}

		String activationConstructorId = taskActivationConstructorManager.addNew(action.getActivation());
		TaskActivationConstructor activationConstructor = taskActivationConstructorManager.getConstructor(activationConstructorId);
		RenderedDetailedUserDefinedAction renderedDetailedUserDefinedAction = RenderedDetailedUserDefinedAction.fromUserDefinedAction(action, activationConstructor);
		return renderTaskDetails(exchange,  activationConstructorId, renderedDetailedUserDefinedAction);
	}

	private Void handleNewHotkey(HttpAsyncExchange exchange, String taskString) throws IOException {
		String activationConstructorId = "";
		if (taskString.equals("record")) {
			KeyChain recordKeyChain = backEndHolder.getConfig().getRECORD();
			activationConstructorId = taskActivationConstructorManager.addNew(TaskActivation.newBuilder().withHotKey(recordKeyChain).build(), TaskActivationConstructor.Config.ofRestricted().setDisableKeyChain(false));
		}
		if (taskString.equals("replay")) {
			KeyChain replayKeyChain = backEndHolder.getConfig().getREPLAY();
			activationConstructorId = taskActivationConstructorManager.addNew(TaskActivation.newBuilder().withHotKey(replayKeyChain).build(), TaskActivationConstructor.Config.ofRestricted().setDisableKeyChain(false));
		}
		if (taskString.equals("runCompiled")) {
			KeyChain runCompiledKeyChain = backEndHolder.getConfig().getCOMPILED_REPLAY();
			activationConstructorId = taskActivationConstructorManager.addNew(TaskActivation.newBuilder().withHotKey(runCompiledKeyChain).build(), TaskActivationConstructor.Config.ofRestricted().setDisableKeyChain(false));
		}
		if (taskString.equals("mouseGestureActivation")) {
			KeyChain mouseGestureKeyChain = new KeyChain(backEndHolder.getConfig().getMouseGestureActivationKey());
			activationConstructorId = taskActivationConstructorManager.addNew(TaskActivation.newBuilder().withHotKey(mouseGestureKeyChain).build(), TaskActivationConstructor.Config.ofRestricted().setDisableKeyChain(false).setMaxStrokes(1));
		}
		if (activationConstructorId.isEmpty()) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Unknown hotkey " + taskString);
		}

		TaskActivationConstructor activationConstructor = taskActivationConstructorManager.getConstructor(activationConstructorId);
		RenderedDetailedUserDefinedAction renderedDetailedUserDefinedAction = RenderedDetailedUserDefinedAction.fromHotkey(taskString, HOTKEY_NAMES.getOrDefault(taskString, ""), activationConstructor);
		return renderTaskDetails(exchange,  activationConstructorId, renderedDetailedUserDefinedAction);
	}

	private Void renderTaskDetails(HttpAsyncExchange exchange, String activationConstructorId, RenderedDetailedUserDefinedAction renderedDetailedUserDefinedAction) throws IOException {
		Map<String, Object> data = new HashMap<>();
		data.put("task", renderedDetailedUserDefinedAction);
		data.put("taskActivationConstructorId", activationConstructorId);
		data.put("activation", renderedDetailedUserDefinedAction.getActivation());
		return renderedPage(exchange, "task_details", data);
	}

	private boolean isHotkey(String taskString) {
		return taskString.equals("record")
				|| taskString.equals("replay")
				|| taskString.equals("runCompiled")
				|| taskString.equals("mouseGestureActivation");
	}
}
