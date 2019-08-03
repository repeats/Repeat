package core.webui.server.handlers.internal.repeatsclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.ipc.repeatClient.repeatPeerClient.RepeatsPeerServiceClient;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.server.handlers.renderedobjects.RenderedRepeatsRemoteClient;
import core.webui.server.handlers.renderedobjects.TooltipsRepeatsRemoteClientPage;

public class RepeatsRemoteClientPageHandler extends AbstractUIHttpHandler {

	public RepeatsRemoteClientPageHandler(ObjectRenderer objectRenderer) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.GET_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
		Map<String, Object> data = new HashMap<>();
		List<RenderedRepeatsRemoteClient> services = new ArrayList<>();
		for (RepeatsPeerServiceClient client : this.backEndHolder.getPeerServiceClientManager().getClients()) {
			services.add(RenderedRepeatsRemoteClient.fromRepeatsPeerServiceClient(client));
		}
		data.put("clients", services);
		data.put("tooltips", new TooltipsRepeatsRemoteClientPage());

		return renderedPage(exchange, "repeats_clients", data);
	}

}
