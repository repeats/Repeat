package core.webui.server.handlers.internals.taskgroups;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.userDefinedTask.TaskGroup;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.CommonTask;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;

public class ActionSwitchTaskGroupHandler extends AbstractUIHttpHandler {

	public ActionSwitchTaskGroupHandler(ObjectRenderer objectRenderer) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
			throws HttpException, IOException {
		Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(request);
		if (params == null) {
			return HttpServerUtilities.prepareTextResponse(exchange, 400, "Unable to get POST parameters.");
		}

		String rendering = params.get("render");
		if (rendering == null) {
			return HttpServerUtilities.prepareTextResponse(exchange, 400, "Missing `render` parameter.");
		}
		if (!rendering.equals("tasks") && !rendering.equals("groups")) {
			return HttpServerUtilities.prepareTextResponse(exchange, 400, "Render parameter only takes `tasks` or `groups`.");
		}

		TaskGroup newCurrent = CommonTask.getTaskGroupFromRequest(backEndHolder, params, false);
		if (newCurrent == null) {
			return HttpServerUtilities.prepareTextResponse(exchange, 400, "Cannot get task group from request.");
		}

		backEndHolder.setCurrentTaskGroup(newCurrent);

		if (rendering.equals("tasks")) {
			return renderedTaskForGroup(exchange);
		} else if (rendering.equals("groups")) {
			return renderedTaskGroups(exchange);
		}
		return null;
	}
}
