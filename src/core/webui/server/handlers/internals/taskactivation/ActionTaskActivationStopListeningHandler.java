package core.webui.server.handlers.internals.taskactivation;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import core.keyChain.TaskActivationConstructor;
import core.keyChain.TaskActivationConstructorManager;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;

public class ActionTaskActivationStopListeningHandler extends AbstractTaskActivationConstructorActionHandler {

	public ActionTaskActivationStopListeningHandler(ObjectRenderer objectRenderer,
			TaskActivationConstructorManager taskActivationConstructorManager) {
		super(objectRenderer, taskActivationConstructorManager);
	}

	@Override
	protected Void handleRequestWithBackendAndConstructor(HttpAsyncExchange exchange,
			TaskActivationConstructor constructor, Map<String, String> params) throws HttpException, IOException {
		constructor.stopListening();
		constructor.clearStrokes();
		return HttpServerUtilities.prepareHttpResponse(exchange, 200, "");
	}
}
