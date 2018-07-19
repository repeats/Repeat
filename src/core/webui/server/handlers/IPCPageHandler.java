package core.webui.server.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.ipc.IIPCService;
import core.ipc.IPCServiceManager;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.server.handlers.renderedobjects.RenderedIPCService;
import core.webui.server.handlers.renderedobjects.TooltipsIPCPage;

public class IPCPageHandler extends AbstractUIHttpHandler {

	public IPCPageHandler(ObjectRenderer objectRenderer) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.GET_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
			throws HttpException, IOException {
		Map<String, Object> data = new HashMap<>();
		List<RenderedIPCService> services = new ArrayList<>(IPCServiceManager.IPC_SERVICE_COUNT);
		for (int i = 0; i < IPCServiceManager.IPC_SERVICE_COUNT; i++) {
			IIPCService service = IPCServiceManager.getIPCService(i);
			services.add(RenderedIPCService.fromIPCService(service));
		}
		data.put("ipcs", services);
		data.put("tooltips", new TooltipsIPCPage());

		return renderedPage(exchange, "ipcs", data);
	}
}
