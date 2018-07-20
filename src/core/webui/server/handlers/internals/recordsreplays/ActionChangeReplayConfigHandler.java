package core.webui.server.handlers.internals.recordsreplays;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import argo.jdom.JsonNode;
import core.webcommon.HttpServerUtilities;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import utilities.NumberUtility;
import utilities.json.JSONUtility;
import utilities.json.Jsonizer;

public class ActionChangeReplayConfigHandler extends AbstractSingleMethodHttpHandler {

	public ActionChangeReplayConfigHandler() {
		super(AbstractSingleMethodHttpHandler.POST_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
			throws HttpException, IOException {
		Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(request);
		if (params == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Failed to get POST parameters.");
		}

		long count;
		long delay;
		float speedup;

		String countString = params.get("count");
		if (countString != null) {
			if (!NumberUtility.isPositiveInteger(countString)) {
				return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Count must be positive integer.");
			}
			count = Long.parseLong(countString);
		} else {
			count = backEndHolder.getReplayConfig().getCount();
		}

		String delayString = params.get("delay");
		if (delayString != null) {
			if (!NumberUtility.isPositiveInteger(delayString)) {
				return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Delay must be non-negative integer.");
			}
			delay = Long.parseLong(delayString);
		} else {
			delay = backEndHolder.getReplayConfig().getDelay();
		}

		String speedupString = params.get("speedup");
		if (speedupString != null) {
			if (!NumberUtility.isDouble(speedupString)) {
				return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Speedup must be a float number.");
			}
			speedup = Float.parseFloat(speedupString);
			if (speedup <= 0) {
				return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Speedup must be a positive float number.");
			}
		} else {
			speedup = backEndHolder.getReplayConfig().getSpeedup();
		}

		backEndHolder.setReplayCount(count);
		backEndHolder.setReplayDelay(delay);
		backEndHolder.setReplaySpeedup(speedup);

		JsonNode responseNode = Jsonizer.jsonize(ResponseMessage.of(count, delay, speedup));
		if (responseNode == null) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Failed to jsonize response.");
		}

		return HttpServerUtilities.prepareHttpResponse(exchange, 200, JSONUtility.jsonToString(responseNode.getRootNode()));
	}

	@SuppressWarnings("unused")
	private static class ResponseMessage {
		private long count;
		private long delay;
		private float speedup;

		private static ResponseMessage of(long count, long delay, float speedup) {
			ResponseMessage output = new ResponseMessage();
			output.count = count;
			output.delay = delay;
			output.speedup = speedup;
			return output;
		}
	}
}
