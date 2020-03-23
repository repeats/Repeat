package core.webui.server.handlers.internals.tasks;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import argo.jdom.JsonNode;
import core.userDefinedTask.internals.RunActionConfig;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.internals.tasks.RunTaskRequest.RunConfig;
import core.webui.webcommon.HttpServerUtilities;
import utilities.NumberUtility;

public class SaveRunTaskConfigHandler extends AbstractSingleMethodHttpHandler {

	public SaveRunTaskConfigHandler() {
		super(AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
		JsonNode requestMessage = HttpServerUtilities.parsePostParameters(request);
		if (requestMessage == null) {
			return HttpServerUtilities.prepareTextResponse(exchange, 400, "Unable to parse JSON from request parameter.");
		}

		RunConfig config = RunConfig.of();
		config.parse(requestMessage);
		String repeatCountString = config.getRepeatCount();
		if (!NumberUtility.isPositiveInteger(repeatCountString)) {
			return HttpServerUtilities.prepareTextResponse(exchange, 400, "Repeat count must be a positive integer.");
		}
		int repeatCount = Integer.parseInt(repeatCountString);

		String delayMsString = config.getDelayMsBetweenRepeat();
		if (!NumberUtility.isNonNegativeInteger(delayMsString)) {
			return HttpServerUtilities.prepareTextResponse(exchange, 400, "Delay in milliseconds must be a non-negative integer.");
		}
		long delayMs = Long.parseLong(delayMsString);

		backEndHolder.setRunActionConfig(RunActionConfig.of(repeatCount, delayMs));
		return HttpServerUtilities.prepareTextResponse(exchange, 200, "");
	}
}
