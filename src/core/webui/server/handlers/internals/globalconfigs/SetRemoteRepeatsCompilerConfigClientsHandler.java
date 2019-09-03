package core.webui.server.handlers.internals.globalconfigs;

import java.io.IOException;
import java.util.List;

import org.apache.http.nio.protocol.HttpAsyncExchange;

import core.webui.server.handlers.renderedobjects.ObjectRenderer;

public class SetRemoteRepeatsCompilerConfigClientsHandler extends AbstractSetRemoteRepeatsClientsHandler {

	public SetRemoteRepeatsCompilerConfigClientsHandler(ObjectRenderer objectRenderer) {
		super(objectRenderer);
	}

	@Override
	public void setConfig(List<String> clientIds) {
		backEndHolder.getConfig().getCompilerFactory().getRemoteRepeatsCompilerConfig().setClients(clientIds);
	}

	@Override
	public Void renderResponse(HttpAsyncExchange exchange) throws IOException {
		return renderedRemoteRepeatsCompilerClientsConfig(exchange);
	}
}
