package core.webui.server.handlers;

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
import core.userDefinedTask.UserDefinedAction;
import core.webcommon.HttpServerUtilities;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.server.handlers.renderedobjects.RenderedTaskGroupButton;
import core.webui.server.handlers.renderedobjects.RenderedUserDefinedAction;
import core.webui.server.handlers.renderedobjects.TooltipsIndexPage;
import utilities.DateUtility;

public class IndexPageHandler extends AbstractUIHttpHandler {

	public IndexPageHandler(ObjectRenderer objectRenderer) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.GET_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
			throws HttpException, IOException {
		Map<String, Object> data = new HashMap<>();

		TaskGroup group = backEndHolder.getCurrentTaskGroup();
		data.put("task_group", RenderedTaskGroupButton.fromTaskGroups(group, backEndHolder.getTaskGroups()));
		List<RenderedUserDefinedAction> taskList = group.getTasks().stream().map(RenderedUserDefinedAction::fromUserDefinedAction).collect(Collectors.toList());
		data.put("tasks", taskList);
		data.put("tooltips", new TooltipsIndexPage());

		data.put("executionTime", getExecutionTime());

		String page = objectRenderer.render("dashboard", data);
		if (page == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Failed to render page.");
		}

		return HttpServerUtilities.prepareHttpResponse(exchange, HttpStatus.SC_OK, page);
	}

	private String getExecutionTime() {
		long time = 0;
		for (TaskGroup group : backEndHolder.getTaskGroups()) {
			for (UserDefinedAction action : group.getTasks()) {
				time += action.getStatistics().getTotalExecutionTime();
			}
		}

		return DateUtility.durationToString(time);
	}
}
