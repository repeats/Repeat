package core.webui.server.handlers.internals.ipcs;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.ipc.IIPCService;
import core.ipc.IPCServiceWithModifablePort;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.CommonTask;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;
import utilities.NumberUtility;

public class ModifyIPCServicePortHandler extends AbstractUIHttpHandler {

	public ModifyIPCServicePortHandler(ObjectRenderer objectRenderer) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
			throws HttpException, IOException {
		Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(request);
		if (params == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Unable to get POST paramters.");
		}

		IIPCService service = CommonTask.getIPCService(params);
		if (service == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Unable to get IPC service.");
		}

		String portString = params.get("port");
		if (portString == null || !NumberUtility.isNonNegativeInteger(portString)) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Port must be non-negative integer.");
		}

		int port = Integer.parseInt(portString);
		if (port > 65535) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Port must be integer between 0 and 65535.");
		}

		if (!(service instanceof IPCServiceWithModifablePort)) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Service port cannot be modified.");
		}

		service.setPort(port);
		return renderedIpcServices(exchange);
	}
}
