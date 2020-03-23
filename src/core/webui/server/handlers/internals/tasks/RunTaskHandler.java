package core.webui.server.handlers.internals.tasks;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import argo.jdom.JsonNode;
import core.userDefinedTask.UserDefinedAction;
import core.userDefinedTask.internals.ActionExecutionRequest;
import core.userDefinedTask.internals.RunActionConfig;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;
import utilities.NumberUtility;

public class RunTaskHandler extends AbstractSingleMethodHttpHandler {

	public RunTaskHandler() {
		super(AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
		JsonNode requestMessage = HttpServerUtilities.parsePostParameters(request);
		if (requestMessage == null) {
			return HttpServerUtilities.prepareTextResponse(exchange, 400, "Unable to parse JSON from request parameter.");
		}

		RunTaskRequest requestData = RunTaskRequest.of();
		if (!requestData.parse(requestMessage)) {
			return HttpServerUtilities.prepareTextResponse(exchange, 400, "Unable to parse POST request parameters.");
		}
		String id = requestData.getId();
		RunActionConfig runConfig = backEndHolder.getRunActionConfig();
		ActionExecutionRequest executionRequest = ActionExecutionRequest.of(runConfig.getRepeatCount(), runConfig.getDelayMsBetweenRepeats());

		if (requestData.getRunConfig() != null) { // Custom run config is provided.
			String repeatCountString = requestData.getRunConfig().getRepeatCount();
			if (!NumberUtility.isPositiveInteger(repeatCountString)) {
				return HttpServerUtilities.prepareTextResponse(exchange, 400, "Repeat count must be a positive integer.");
			}
			int repeatCount = Integer.parseInt(repeatCountString);

			String delayMsString = requestData.getRunConfig().getDelayMsBetweenRepeat();
			if (!NumberUtility.isNonNegativeInteger(delayMsString)) {
				return HttpServerUtilities.prepareTextResponse(exchange, 400, "Delay in milliseconds must be a non-negative integer.");
			}
			long delayMs = Long.parseLong(delayMsString);
			executionRequest = ActionExecutionRequest.of(repeatCount, delayMs);
		}

		UserDefinedAction action = backEndHolder.getTask(id);
		if (action == null) {
			return HttpServerUtilities.prepareTextResponse(exchange, 404, "No such task with ID " + id + ".");
		}
		backEndHolder.getActionExecutor().startExecutingAction(executionRequest, action);

		return HttpServerUtilities.prepareTextResponse(exchange, 200, "");
	}
}
