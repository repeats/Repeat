package core.webui.server.handlers.internals.taskactivation;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import core.keyChain.TaskActivationConstructor;
import core.keyChain.TaskActivationConstructorManager;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;

public class ActionTaskActivationSetGlobalKeyAction extends AbstractTaskActivationConstructorActionHandler {

	public ActionTaskActivationSetGlobalKeyAction(ObjectRenderer objectRenderer, TaskActivationConstructorManager taskActivationConstructorManager) {
		super(objectRenderer, taskActivationConstructorManager);
	}

	@Override
	protected Void handleRequestWithBackendAndConstructor(HttpAsyncExchange exchange, TaskActivationConstructor constructor, Map<String, String> params) throws HttpException, IOException {
		if (!params.containsKey("pressed") && !params.containsKey("released")) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "At least one of pressed or released must be set.");
		}
		if (params.containsKey("pressed") && !isBooleanValue(params.get("pressed"))) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Value of pressed must be boolean.");
		}
		if (params.containsKey("released") && !isBooleanValue(params.get("released"))) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Value of released must be boolean.");
		}

		if (params.containsKey("pressed")) {
			boolean value = params.get("pressed").equals("true");
			constructor.setGlobalKeyPressed(value);
		}
		if (params.containsKey("released")) {
			boolean value = params.get("released").equals("true");
			constructor.setGlobalKeyReleased(value);
		}

		return emptySuccessResponse(exchange);
	}

	private boolean isBooleanValue(String value) {
		return value.equals("true") || value.equals("false");
	}
}
