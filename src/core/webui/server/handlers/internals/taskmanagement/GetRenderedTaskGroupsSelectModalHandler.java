package core.webui.server.handlers.internals.taskmanagement;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.userDefinedTask.TaskGroup;
import core.webcommon.HttpServerUtilities;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.AbstractUIHttpHandler;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.server.handlers.renderedobjects.RenderedTaskGroupForSelectModal;

public class GetRenderedTaskGroupsSelectModalHandler extends AbstractUIHttpHandler {

	public GetRenderedTaskGroupsSelectModalHandler(ObjectRenderer objectRenderer) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.GET_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
			throws HttpException, IOException {
		Map<String, Object> data = new HashMap<>();
		TaskGroup group = backEndHolder.getCurrentTaskGroup();
		List<TaskGroup> groups = backEndHolder.getTaskGroups();
		data.put("groups", groups.stream().map(g -> RenderedTaskGroupForSelectModal.fromTaskGroups(g, group)).collect(Collectors.toList()));

		String page = objectRenderer.render("rendered_task_groups_select_modal", data);
		if (page == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Failed to render page.");
		}

		return HttpServerUtilities.prepareHttpResponse(exchange, HttpStatus.SC_OK, page);
	}
}
