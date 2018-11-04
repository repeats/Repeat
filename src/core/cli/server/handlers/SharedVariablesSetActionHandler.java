package core.cli.server.handlers;

import java.io.IOException;

import org.apache.http.nio.protocol.HttpAsyncExchange;

import argo.jdom.JsonNode;
import core.cli.messages.SharedVariablesSetMessage;
import core.cli.server.CliRpcCodec;
import core.userDefinedTask.SharedVariables;

public class SharedVariablesSetActionHandler extends SharedVariablesActionHandler {

	@Override
	protected Void handleSharedVariablesActionWithBackend(HttpAsyncExchange exchange, JsonNode requestData) throws IOException {
		SharedVariablesSetMessage message = SharedVariablesSetMessage.parseJSON(requestData);
		String namespace = message.getNamespace();
		if (namespace == null || namespace.isEmpty()) {
			namespace = SharedVariables.GLOBAL_NAMESPACE;
		}

		String variable = message.getVariable();
		if (variable == null || variable.isEmpty()) {
			return CliRpcCodec.prepareResponse(exchange, 400, "Empty variable name.");
		}

		String value = message.getValue();
		if (value != null) {
			SharedVariables.setVar(namespace, variable, value);
		}

		return CliRpcCodec.prepareResponse(exchange, 200, "");
	}
}
