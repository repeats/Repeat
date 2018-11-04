package core.cli.server.handlers;

import java.io.IOException;

import org.apache.http.nio.protocol.HttpAsyncExchange;

import argo.jdom.JsonNode;
import core.cli.messages.SharedVariablesGetMessage;
import core.cli.server.CliRpcCodec;
import core.userDefinedTask.SharedVariables;

public class SharedVariablesGetActionHandler extends SharedVariablesActionHandler {

	@Override
	protected Void handleSharedVariablesActionWithBackend(HttpAsyncExchange exchange, JsonNode requestData) throws IOException {
		SharedVariablesGetMessage message = SharedVariablesGetMessage.parseJSON(requestData);
		String namespace = message.getNamespace();
		if (namespace == null || namespace.isEmpty()) {
			namespace = SharedVariables.GLOBAL_NAMESPACE;
		}

		String variable = message.getVariable();
		if (variable == null || variable.isEmpty()) {
			return CliRpcCodec.prepareResponse(exchange, 400, "Empty variable name.");
		}

		String data = SharedVariables.getVar(namespace, variable);
		if (data == null) {
			return CliRpcCodec.prepareResponse(exchange, 404, "Variable not found.");
		}

		return CliRpcCodec.prepareResponse(exchange, 200, data);
	}
}
