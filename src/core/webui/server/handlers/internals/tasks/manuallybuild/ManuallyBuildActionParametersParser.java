package core.webui.server.handlers.internals.tasks.manuallybuild;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import core.userDefinedTask.manualBuild.ManuallyBuildStep;
import core.userDefinedTask.manualBuild.steps.ControllerDelayStep;
import core.userDefinedTask.manualBuild.steps.KeyboardPressKeyStep;
import core.userDefinedTask.manualBuild.steps.KeyboardReleaseKeyStep;
import core.userDefinedTask.manualBuild.steps.KeyboardTypeKeyStep;
import core.userDefinedTask.manualBuild.steps.KeyboardTypeStringStep;
import core.userDefinedTask.manualBuild.steps.MouseClickCurrentPositionStep;
import core.userDefinedTask.manualBuild.steps.MouseClickStep;
import core.userDefinedTask.manualBuild.steps.MouseMoveByStep;
import core.userDefinedTask.manualBuild.steps.MouseMoveStep;
import core.userDefinedTask.manualBuild.steps.MousePressCurrentPositionStep;
import core.userDefinedTask.manualBuild.steps.MouseReleaseCurrentPositionStep;
import core.webui.server.handlers.internals.tasks.manuallybuild.ManuallyBuildActionFeModel.Actor;
import core.webui.server.handlers.internals.tasks.manuallybuild.ManuallyBuildActionFeModel.ControllerAction;
import core.webui.server.handlers.internals.tasks.manuallybuild.ManuallyBuildActionFeModel.KeyboardAction;
import core.webui.server.handlers.internals.tasks.manuallybuild.ManuallyBuildActionFeModel.MouseAction;
import utilities.NumberUtility;

public class ManuallyBuildActionParametersParser {

	public static ManuallyBuildActionParametersParser of() {
		return new ManuallyBuildActionParametersParser();
	}

	public ManuallyBuildStep parse(String actor, String action, String paramsString) throws InvalidManuallyBuildComponentException {
		List<String> params = Arrays.asList(paramsString.split(",")).stream().map(x -> x.trim()).collect(Collectors.toList());

		if (actor.equals(Actor.MOUSE.toString())) {
			return parseMouseStep(action, params);
		} else if (actor.equals(Actor.KEYBOARD.toString())) {
			return parseKeyboardStep(action, params);
		} else if (actor.equals(Actor.CONTROLLER.toString())) {
			return parseControllerStep(action, params);
		} else {
			throw new InvalidManuallyBuildComponentException("Unknown actor " + actor + ".");
		}
	}

	private ManuallyBuildStep parseMouseStep(String action, List<String> params) throws InvalidManuallyBuildComponentException {
		if (action.equals(MouseAction.CLICK.toString())) {
			verify(params, Arrays.asList(MouseMask.class, NonNegativeLong.class, NonNegativeLong.class));
			return MouseClickStep.of(MouseMask.getCode(params.get(0)), Integer.parseInt(params.get(1)), Integer.parseInt(params.get(2)));
		} else if (action.equals(MouseAction.CLICK_CURRENT_POSITION.toString())) {
			verify(params, Arrays.asList(MouseMask.class));
			return MouseClickCurrentPositionStep.of(MouseMask.getCode(params.get(0)));
		} else if (action.equals(MouseAction.MOVE_BY.toString())) {
			verify(params, Arrays.asList(Long.class, Long.class));
			return MouseMoveByStep.of(Integer.parseInt(params.get(0)), Integer.parseInt(params.get(1)));
		} else if (action.equals(MouseAction.MOVE.toString())) {
			verify(params, Arrays.asList(NonNegativeLong.class, NonNegativeLong.class));
			return MouseMoveStep.of(Integer.parseInt(params.get(0)), Integer.parseInt(params.get(1)));
		} else if (action.equals(MouseAction.PRESS_CURRENT_POSITION.toString())) {
			verify(params, Arrays.asList(MouseMask.class));
			return MousePressCurrentPositionStep.of(MouseMask.getCode(params.get(0)));
		} else if (action.equals(MouseAction.RELEASE_CURRENT_POSITION.toString())) {
			verify(params, Arrays.asList(MouseMask.class));
			return MouseReleaseCurrentPositionStep.of(MouseMask.getCode(params.get(0)));
		} else {
			throw new InvalidManuallyBuildComponentException("Unknown mouse action " + action + ".");
		}
	}

	private ManuallyBuildStep parseKeyboardStep(String action, List<String> params) throws InvalidManuallyBuildComponentException {
		if (action.equals(KeyboardAction.PRESS_KEY.toString())) {
			verify(params, Arrays.asList(Key.class));
			return KeyboardPressKeyStep.of(Key.getCode(params.get(0)));
		} else if (action.equals(KeyboardAction.RELEASE_KEY.toString())) {
			verify(params, Arrays.asList(Key.class));
			return KeyboardReleaseKeyStep.of(Key.getCode(params.get(0)));
		} else if (action.equals(KeyboardAction.TYPE_KEY.toString())) {
			verify(params, Arrays.asList(Key.class));
			return KeyboardTypeKeyStep.of(Key.getCode(params.get(0)));
		} else if (action.equals(KeyboardAction.TYPE_STRING_KEY.toString())) {
			verify(params, Arrays.asList(String.class));
			return KeyboardTypeStringStep.of(params.get(0));
		} else {
			throw new InvalidManuallyBuildComponentException("Unknown keyboard action " + action + ".");
		}
	}

	private ManuallyBuildStep parseControllerStep(String action, List<String> params) throws InvalidManuallyBuildComponentException {
		if  (action.equals(ControllerAction.WAIT.toString())) {
			verify(params, Arrays.asList(NonNegativeLong.class));
			return ControllerDelayStep.of(Integer.parseInt(params.get(0)));
		}
		throw new InvalidManuallyBuildComponentException("Unknown controller action " + action + ".");
	}

	private static final List<Class<?>> KNOWN_CLASSES = Arrays.asList(
			Long.class,
			NonNegativeLong.class,
			MouseMask.class,
			Key.class,
			String.class
			);

	private void verify(List<String> params, List<Class<?>> required) throws InvalidManuallyBuildComponentException {
		if (params.size() != required.size()) {
			throw new InvalidManuallyBuildComponentException("Got " + params.size() + " parameters but expected " + required.size() + ".");
		}

		Iterator<Class<?>> requiredIterator = required.iterator();
		for (ListIterator<String> iterator = params.listIterator(); iterator.hasNext();) {
			Class<?> requiredClass = requiredIterator.next();
			if (!KNOWN_CLASSES.contains(requiredClass)) {
				throw new InvalidManuallyBuildComponentException("Only accepting Long & String, but got " + requiredClass + ".");
			}

			int index = iterator.nextIndex();
			String param = iterator.next();
			if (requiredClass == Long.class || requiredClass == NonNegativeLong.class) {
				try {
					long number = Long.parseLong(param);
					if (requiredClass == NonNegativeLong.class && number < 0) {
						throw new InvalidManuallyBuildComponentException("Parameter #" + index + " must be a non-negative integer but got " + param);
					}
				} catch (NumberFormatException e) {
					throw new InvalidManuallyBuildComponentException("Parameter #" + index + " must be an integer but got " + param);
				}
			}
			if (requiredClass == MouseMask.class) {
				try {
					MouseMask.verify(param);
				} catch (InvalidManuallyBuildComponentException e) {
					throw new InvalidManuallyBuildComponentException("Invalid mouse mask " + param + ".");
				}
			}
			if (requiredClass == Key.class) {
				try {
					Key.verify(param);
				} catch (InvalidManuallyBuildComponentException e) {
					throw new InvalidManuallyBuildComponentException("Invalid mouse mask " + param + ".");
				}
			}
		}
	}

	private static class MouseMask {
		private static void verify(String value) throws InvalidManuallyBuildComponentException {
			if (!NumberUtility.isNonNegativeInteger(value) && !StringToAwtEventCode.isValidMouseMask(value)) {
				throw new InvalidManuallyBuildComponentException("Unknown mouse with value " + value + ".");
			}
		}

		private static int getCode(String value) throws InvalidManuallyBuildComponentException {
			if (NumberUtility.isNonNegativeInteger(value)) {
				return Integer.parseInt(value);
			}

			return StringToAwtEventCode.mouseMaskFromString(value);
		}
	}

	private static class Key {
		private static void verify(String value) throws InvalidManuallyBuildComponentException {
			if (!NumberUtility.isNonNegativeInteger(value) && !StringToAwtEventCode.isValidKeyValue(value)) {
				throw new InvalidManuallyBuildComponentException("Unknown key with value " + value + ".");
			}
		}

		private static int getCode(String value) throws InvalidManuallyBuildComponentException {
			if (NumberUtility.isNonNegativeInteger(value)) {
				return Integer.parseInt(value);
			}

			return StringToAwtEventCode.keyCodeFromString(value);
		}

	}
	private static class NonNegativeLong {}

	private ManuallyBuildActionParametersParser() {}
}
