package core.webui.server.handlers.internals.tasks.manuallybuild;

import java.util.Arrays;
import java.util.List;

import core.webui.server.handlers.internals.tasks.manuallybuild.ManuallyBuildActionFeModel.Actor;
import core.webui.server.handlers.internals.tasks.manuallybuild.ManuallyBuildActionFeModel.ControllerAction;
import core.webui.server.handlers.internals.tasks.manuallybuild.ManuallyBuildActionFeModel.KeyboardAction;
import core.webui.server.handlers.internals.tasks.manuallybuild.ManuallyBuildActionFeModel.MouseAction;
import utilities.NumberUtility;

public class ManuallyBuildActionParametersSuggestionProvider {

	public static SuggestionResult suggest(String actor, String action, String params) throws InvalidManuallyBuildComponentException {
		if (!ManuallyBuildActionFeModel.of().actionsForActor(actor).contains(action)) {
			throw new InvalidManuallyBuildComponentException("There is no action " + action + " for actor " + actor + ".");
		}

		if (actor.equals(Actor.MOUSE.toString())) {
			return suggestMouse(action, params);
		} else if (actor.equals(Actor.KEYBOARD.toString())) {
			return suggestKeyboard(action, params);
		} else if (actor.equals(Actor.CONTROLLER.toString())) {
			return suggestController(action, params);
		}

		return SuggestionResult.of(true, "");
	}

	private static SuggestionResult suggestMouse(String action, String params) throws InvalidManuallyBuildComponentException {
		List<String> allMasks = StringToAwtEventCode.allSupportedMasks();
		if (action.equals(MouseAction.CLICK.toString())) {

		} else if (action.equals(MouseAction.CLICK_CURRENT_POSITION.toString())) {
			if (params.isEmpty()) {
				return SuggestionResult.of(true, allMasks);
			}
			return SuggestionResult.of(StringToAwtEventCode.isValidMouseMask(params), allMasks);
		} else if (action.equals(MouseAction.MOVE_BY.toString())) {
			if (params.isEmpty()) {
				return SuggestionResult.of(true, "0, 0");
			}

			String[] parts = params.split(",");
			if (parts.length == 1) {
				if (!NumberUtility.isNonNegativeInteger(parts[0].trim())) {
					return SuggestionResult.of(false, "0, 0");
				}

				return SuggestionResult.of(true, parts[0] + ", 0");
			} else if (parts.length == 2) {
				if (!NumberUtility.isNonNegativeInteger(parts[0].trim())) {
					return SuggestionResult.of(false, "0, 0");
				}
				if (!NumberUtility.isNonNegativeInteger(parts[1].trim())) {
					return SuggestionResult.of(false, parts[0] + ", 0");
				}
				return SuggestionResult.of(true, params);
			}
		} else if (action.equals(MouseAction.MOVE.toString())) {
			if (params.isEmpty()) {
				return SuggestionResult.of(true, "0, 0");
			}

		} else if (action.equals(MouseAction.PRESS_CURRENT_POSITION.toString())) {
			if (params.isEmpty()) {
				return SuggestionResult.of(true, allMasks);
			}
			return SuggestionResult.of(StringToAwtEventCode.isValidMouseMask(params), allMasks);
		} else if (action.equals(MouseAction.RELEASE_CURRENT_POSITION.toString())) {
			if (params.isEmpty()) {
				return SuggestionResult.of(true, allMasks);
			}
			return SuggestionResult.of(StringToAwtEventCode.isValidMouseMask(params), allMasks);
		}

		throw new InvalidManuallyBuildComponentException("There is no action " + action + " for actor controller.");
	}

	private static SuggestionResult suggestKeyboard(String action, String params) throws InvalidManuallyBuildComponentException {
		if (action.equals(KeyboardAction.PRESS_KEY.toString())) {

		} else if (action.equals(KeyboardAction.RELEASE_KEY.toString())) {

		} else if (action.equals(KeyboardAction.TYPE_KEY.toString())) {

		} else if (action.equals(KeyboardAction.TYPE_STRING_KEY.toString())) {

		}

		throw new InvalidManuallyBuildComponentException("There is no action " + action + " for actor controller.");
	}

	private static SuggestionResult suggestController(String action, String params) throws InvalidManuallyBuildComponentException {
		if (action.equals(ControllerAction.WAIT.toString())) {
			if (params.isEmpty()) {
				return SuggestionResult.of(true, Arrays.asList("100"));
			}

			if (!NumberUtility.isNonNegativeInteger(params)) {
				return SuggestionResult.of(false, "100");
			}
		}
		throw new InvalidManuallyBuildComponentException("There is no action " + action + " for actor controller.");
	}

	public static class SuggestionResult {
		boolean valid;
		List<String> suggestions;

		public boolean isValid() {
			return valid;
		}

		public List<String> getSuggestions() {
			return suggestions;
		}

		private static SuggestionResult of(boolean valid, String suggestion) {
			return of(valid, Arrays.asList(suggestion));
		}

		private static SuggestionResult of(boolean valid, List<String>  suggestions) {
			SuggestionResult result = new SuggestionResult();
			result.valid = valid;
			result.suggestions = suggestions;
			return result;
		}

		private SuggestionResult() {}
	}

	private ManuallyBuildActionParametersSuggestionProvider() {}
}
