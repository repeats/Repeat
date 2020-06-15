package core.webui.server.handlers.internals.tasks.manuallybuild;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.internals.tasks.manuallybuild.ManuallyBuildActionParametersSuggestionProvider.SuggestionResult;
import core.webui.webcommon.HttpServerUtilities;
import utilities.ExceptionsUtility;
import utilities.json.Jsonizer;

public class ActionManuallyBuildActionListParameterSuggestionsHandler extends AbstractSingleMethodHttpHandler {

	public ActionManuallyBuildActionListParameterSuggestionsHandler() {
		super(AbstractSingleMethodHttpHandler.GET_METHOD);
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
		Map<String, String> parameters = HttpServerUtilities.parseGetParameters(request.getRequestLine().getUri());
		if (parameters == null) {
			return HttpServerUtilities.prepareTextResponse(exchange, 400, "Unable to parse GET parameters.");
		}
		String actor = parameters.get("actor");
		if (actor == null) {
			return HttpServerUtilities.prepareTextResponse(exchange, 400, "Actor must be provided.");
		}
		String action = parameters.get("action");
		if (action == null) {
			return HttpServerUtilities.prepareTextResponse(exchange, 400, "Action must be provided.");
		}
		String value = parameters.get("params");
		if (value == null) {
			return HttpServerUtilities.prepareTextResponse(exchange, 400, "Params must be provided.");
		}
		actor = actor.trim().toLowerCase();
		action = action.trim().toLowerCase();

		try {
			SuggestionResult result = ManuallyBuildActionParametersSuggestionProvider.suggest(actor, action, value);
			return HttpServerUtilities.prepareJsonResponse(exchange, 200, Jsonizer.jsonize(Suggestions.of(result.valid, result.suggestions)));
		} catch (InvalidManuallyBuildComponentException e) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, ExceptionsUtility.getStackTrace(e));
		}
	}

	@SuppressWarnings("unused")
	private static final class Suggestions {
		private boolean valid;
		private List<String> suggestions;

		private static Suggestions of(boolean valid, List<String> suggestions) {
			Suggestions output = new Suggestions();
			output.valid = valid;
			output.suggestions = new ArrayList<>();
			output.suggestions.addAll(suggestions);
			return output;
		}
	}
}
