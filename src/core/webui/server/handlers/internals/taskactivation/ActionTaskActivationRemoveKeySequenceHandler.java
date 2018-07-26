package core.webui.server.handlers.internals.taskactivation;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import core.keyChain.TaskActivationConstructor;
import core.keyChain.TaskActivationConstructorManager;
import core.webcommon.HttpServerUtilities;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import utilities.NumberUtility;

public class ActionTaskActivationRemoveKeySequenceHandler extends AbstractTaskActivationConstructorActionHandler {

	public ActionTaskActivationRemoveKeySequenceHandler(ObjectRenderer objectRenderer,
			TaskActivationConstructorManager taskActivationConstructorManager) {
		super(objectRenderer, taskActivationConstructorManager);
	}

	@Override
	protected Void handleRequestWithBackendAndConstructor(HttpAsyncExchange exchange,
			TaskActivationConstructor constructor, Map<String, String> params) throws HttpException, IOException {
		String index = params.get("index");
		if (!NumberUtility.isNonNegativeInteger(index)) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Index must be non-negative integer.");
		}

		constructor.removeKeySequence(Integer.parseInt(index));
		return renderedTaskActivationPage(exchange, "fragments/key_sequences", constructor.getActivation());
	}
}
