package core.webui.server.handlers.internals.taskactivation;

import java.awt.event.InputEvent;
import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import core.keyChain.MouseKey;
import core.keyChain.TaskActivationConstructor;
import core.keyChain.TaskActivationConstructorManager;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.webcommon.HttpServerUtilities;

public class ActionTaskActivationAddMouseKey extends AbstractTaskActivationConstructorActionHandler {

	public ActionTaskActivationAddMouseKey(ObjectRenderer objectRenderer,
			TaskActivationConstructorManager taskActivationConstructorManager) {
		super(objectRenderer, taskActivationConstructorManager);
	}

	@Override
	protected Void handleRequestWithBackendAndConstructor(HttpAsyncExchange exchange,
			TaskActivationConstructor constructor, Map<String, String> params) throws HttpException, IOException {
		if (!constructor.isListening()) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Enable key listening before adding mouse click.");
		}
		if (!params.containsKey("key")) {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Request missing the 'key' parameter.");
		}
		String key = params.get("key");
		int mouseKey = 0;
		if (key.equals("LEFT")) {
			mouseKey = InputEvent.BUTTON1_DOWN_MASK;
		} else if (key.equals("RIGHT")) {
			mouseKey = InputEvent.BUTTON3_DOWN_MASK;
		} else if (key.equals("MIDDLE")) {
			mouseKey = InputEvent.BUTTON2_DOWN_MASK;
		} else {
			return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Request 'key' must be either 'LEFT', 'RIGHT', or 'MIDDLE'.");
		}
		constructor.addMouseKey(MouseKey.of(mouseKey));

		String strokes = constructor.getStrokes();
		return HttpServerUtilities.prepareHttpResponse(exchange, 200, strokes.isEmpty() ? "None" : strokes);
	}
}
