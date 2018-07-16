package core.webui.server.handlers.internals.ipcs;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.ipc.IIPCService;
import core.webcommon.HttpServerUtilities;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.CommonTask;

public class ActionRunIPCServiceHandler extends AbstractSingleMethodHttpHandler {

	public ActionRunIPCServiceHandler() {
		super(AbstractSingleMethodHttpHandler.POST_METHOD);
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

		try {
			service.startRunning();
		} catch (IOException e) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Unable to start IPC service.");
		}
		return HttpServerUtilities.prepareHttpResponse(exchange, 200, "");
	}

}
