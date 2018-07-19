package core.webui.server.handlers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.userDefinedTask.TaskGroup;
import core.userDefinedTask.UserDefinedAction;
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
		data.put("taskGroup", RenderedTaskGroupButton.fromTaskGroups(group, backEndHolder.getTaskGroups()));
		List<RenderedUserDefinedAction> taskList = group.getTasks().stream().map(RenderedUserDefinedAction::fromUserDefinedAction).collect(Collectors.toList());
		data.put("tasks", taskList);
		data.put("tooltips", new TooltipsIndexPage());

		data.put("executionTime", getExecutionTime());

		return renderedPage(exchange, "index", data);
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
