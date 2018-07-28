package core.webui.server.handlers.internals.taskactivation;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import core.keyChain.TaskActivationConstructor;
import core.keyChain.TaskActivationConstructorManager;
import core.webcommon.HttpServerUtilities;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;

public class ActionTaskActivationAddPhraseHandler extends AbstractTaskActivationConstructorActionHandler {

	public ActionTaskActivationAddPhraseHandler(ObjectRenderer objectRenderer,
			TaskActivationConstructorManager taskActivationConstructorManager) {
		super(objectRenderer, taskActivationConstructorManager);
	}

	@Override
	protected Void handleRequestWithBackendAndConstructor(HttpAsyncExchange exchange,
			TaskActivationConstructor constructor, Map<String, String> params) throws HttpException, IOException {
		String phrase = params.get("phrase");
		if (phrase == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "No phrase provided.");
		}
		constructor.addPhrase(phrase);
		return renderedTaskActivationPage(exchange, "fragments/phrases", constructor);
	}
}
