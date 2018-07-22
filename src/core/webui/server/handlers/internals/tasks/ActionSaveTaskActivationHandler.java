package core.webui.server.handlers.internals.tasks;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import core.keyChain.TaskActivation;
import core.keyChain.TaskActivationConstructor;
import core.keyChain.TaskActivationConstructorManager;
import core.userDefinedTask.UserDefinedAction;
import core.webcommon.HttpServerUtilities;
import core.webui.server.handlers.CommonTask;
import core.webui.server.handlers.internals.taskactivation.AbstractTaskActivationConstructorActionHandler;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;

public class ActionSaveTaskActivationHandler extends AbstractTaskActivationConstructorActionHandler {

	public ActionSaveTaskActivationHandler(ObjectRenderer objectRenderer,
			TaskActivationConstructorManager taskActivationConstructorManager) {
		super(objectRenderer, taskActivationConstructorManager);
	}

	@Override
	protected Void handleRequestWithBackendAndConstructor(HttpAsyncExchange exchange,
			TaskActivationConstructor constructor, Map<String, String> params) throws HttpException, IOException {
		UserDefinedAction task = CommonTask.getTaskFromRequest(backEndHolder, params);
		if (task == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Cannot get task from request.");
		}

		TaskActivation activation = constructor.getActivation();
		task.setActivation(activation);

		taskActivationConstructorManager.remove(params.get("id"));
		return HttpServerUtilities.prepareHttpResponse(exchange, 200, "");
	}
}
