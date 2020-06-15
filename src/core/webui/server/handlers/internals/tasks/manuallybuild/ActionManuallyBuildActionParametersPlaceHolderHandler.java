package core.webui.server.handlers.internals.tasks.manuallybuild;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.internals.tasks.manuallybuild.ManuallyBuildActionFeModel.Actor;
import core.webui.server.handlers.internals.tasks.manuallybuild.ManuallyBuildActionFeModel.ControllerAction;
import core.webui.server.handlers.internals.tasks.manuallybuild.ManuallyBuildActionFeModel.KeyboardAction;
import core.webui.server.handlers.internals.tasks.manuallybuild.ManuallyBuildActionFeModel.MouseAction;
import core.webui.webcommon.HttpServerUtilities;
import utilities.ExceptionsUtility;

public class ActionManuallyBuildActionParametersPlaceHolderHandler extends AbstractSingleMethodHttpHandler {

	public ActionManuallyBuildActionParametersPlaceHolderHandler() {
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
		actor = actor.toLowerCase();
		action = action.toLowerCase();

		try {
			String placeholder = placeHolderParametersText(actor, action);
			return HttpServerUtilities.prepareHttpResponse(exchange, 200, placeholder);
		} catch (InvalidManuallyBuildComponentException e) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, ExceptionsUtility.getStackTrace(e));
		}
	}

	private String placeHolderParametersText(String actor, String action) throws InvalidManuallyBuildComponentException {
		if (actor.equals(Actor.MOUSE.toString())) {
			String maskPlaceholder = "<mask (left/middle/right)>";

			if (action.equals(MouseAction.CLICK.toString())) {
				return maskPlaceholder + ",<x coordinate>,<y coordinate>";
			} else if (action.equals(MouseAction.CLICK_CURRENT_POSITION.toString())) {
				return maskPlaceholder;
			} else if (action.equals(MouseAction.MOVE_BY.toString())) {
				return "<x pixels>,<y pixels>";
			} else if (action.equals(MouseAction.MOVE.toString())) {
				return "<x coordinate>,<y coordinate>";
			} else if (action.equals(MouseAction.PRESS_CURRENT_POSITION.toString())) {
				return maskPlaceholder;
			} else if (action.equals(MouseAction.RELEASE_CURRENT_POSITION.toString())) {
				return maskPlaceholder;
			} else {
				throw new InvalidManuallyBuildComponentException("Unknown action " + action + " for actor " + actor + ".");
			}
		} else if (actor.equals(Actor.KEYBOARD.toString())) {
			String keyPlaceholder = "<key (A/B/C/DELETE/1/....)>";
			String multiKeysPlaceholder = keyPlaceholder + ",<key2>,<key3>,...";

			if (action.equals(KeyboardAction.PRESS_KEY.toString())) {
				return multiKeysPlaceholder;
			} else if (action.equals(KeyboardAction.RELEASE_KEY.toString())) {
				return multiKeysPlaceholder;
			} else if (action.equals(KeyboardAction.TYPE_KEY.toString())) {
				return multiKeysPlaceholder;
			} else if (action.equals(KeyboardAction.TYPE_STRING_KEY.toString())) {
				return "<any text string>";
			} else {
				throw new InvalidManuallyBuildComponentException("Unknown action " + action + " for actor " + actor + ".");
			}
		} else if (actor.equals(Actor.CONTROLLER.toString())) {
			if (action.equals(ControllerAction.WAIT.toString())) {
				return "<wait time in milliseconds>";
			} else {
				throw new InvalidManuallyBuildComponentException("Unknown action " + action + " for actor " + actor + ".");
			}
		} else {
			throw new InvalidManuallyBuildComponentException("Unknown actor " + actor + ".");
		}
	}
}
