package core.webui.server.handlers.internals.tasks.manuallybuild;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.server.handlers.renderedobjects.RenderedPossibleManuallyBuildActions;
import core.webui.webcommon.HttpServerUtilities;

public class ActionManuallyBuildActionListActionsForActorHandler extends AbstractUIHttpHandler {

	public ActionManuallyBuildActionListActionsForActorHandler(ObjectRenderer objectRenderer) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.GET_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
		Map<String, String> params = HttpServerUtilities.parseGetParameters(request.getRequestLine().getUri());
		if (!params.containsKey("actor")) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "No actor provided.");
		}
		String actor = params.get("actor").toLowerCase();
		List<String> actions = ManuallyBuildActionFeModel.of().actionsForActor(actor);
		Map<String, Object> data = new HashMap<>();
		data.put("possibleActions", RenderedPossibleManuallyBuildActions.of(actions));

		String page = objectRenderer.render("fragments/manually_build_task_actions_rendered", data);
		if (page == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Failed to render page.");
		}

		return HttpServerUtilities.prepareHttpResponse(exchange, HttpStatus.SC_OK, page);
	}
}
