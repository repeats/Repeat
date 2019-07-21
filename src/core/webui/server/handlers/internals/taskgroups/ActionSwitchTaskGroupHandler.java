package core.webui.server.handlers.internals.taskgroups;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.userDefinedTask.TaskGroup;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;
import utilities.NumberUtility;

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

		String groupIndex = params.get("group");
		if (groupIndex == null || !NumberUtility.isNonNegativeInteger(groupIndex)) {
			return HttpServerUtilities.prepareTextResponse(exchange, 400, "Group index must be provided as non-negative integer.");
		}

		int index = Integer.parseInt(groupIndex);
		List<TaskGroup> groups = backEndHolder.getTaskGroups();
		if (index >= groups.size()) {
			return HttpServerUtilities.prepareTextResponse(exchange, 400, "Group index out of bound.");
		}

		backEndHolder.setCurrentTaskGroup(groups.get(index));

		if (rendering.equals("tasks")) {
			return renderedTaskForGroup(exchange);
		} else if (rendering.equals("groups")) {
			return renderedTaskGroups(exchange);
		}
		return null;
	}
}
