package core.webui.server.handlers.internals.tasks.manuallybuild;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
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

public class ManuallyBuildActionParametersParser {

	public static enum Actor {
		MOUSE("mouse"),
		KEYBOARD("keyboard"),
		CONTROLLER("controller")
		;

		private String value;
		private Actor(String s) { value = s; }
		@Override
		public String toString() {
			return value;
		}

		private static Actor forValue(String s) {
			for (Actor actor : Actor.values()) {
				if (actor.toString().equals(s)) {
					return actor;
				}
			}
			return null;
		}
	}

	private static final Map<Actor, List<Action>> ACTORS_TO_ACTIONS = new HashMap<>();
	static {
		ACTORS_TO_ACTIONS.put(Actor.MOUSE, Arrays.asList(MouseAction.values()));
		ACTORS_TO_ACTIONS.put(Actor.KEYBOARD, Arrays.asList(KeyboardAction.values()));
		ACTORS_TO_ACTIONS.put(Actor.CONTROLLER, Arrays.asList(ControllerAction.values()));
	}

	public static ManuallyBuildActionParametersParser of() {
		return new ManuallyBuildActionParametersParser();
	}

	public static interface Action {
		@Override
		public abstract String toString();
	}

	public List<String> noAction() {
		return new ArrayList<>(0);
	}

	public List<String> actionsForActor(String actorValue) {
		Actor actor = Actor.forValue(actorValue);
		return ACTORS_TO_ACTIONS.getOrDefault(actor, Arrays.asList()).stream().map(Action::toString).collect(Collectors.toList());
	}

	public ManuallyBuildStep parse(String actor, String action, String paramsString) throws InvalidSuppliedBuildStepException {
		List<String> params = Arrays.asList(paramsString.split(","));

		if (actor.equals(Actor.MOUSE.toString())) {
			return parseMouseStep(action, params);
		} else if (actor.equals(Actor.KEYBOARD.toString())) {
			return parseKeyboardStep(action, params);
		} else if (actor.equals(Actor.CONTROLLER.toString())) {
			return parseControllerStep(action, params);
		} else {
			throw new InvalidSuppliedBuildStepException("Unknown actor " + actor + ".");
		}
	}

	private static enum MouseAction implements Action {
		CLICK("click"),
		CLICK_CURRENT_POSITION("click_current_position"),
		MOVE_BY("move_by"),
		MOVE("move"),
		PRESS_CURRENT_POSITION("press_current_position"),
		RELEASE_CURRENT_POSITION("release_current_position")
		;

		private String value;
		private MouseAction(String s) { value = s; }
		@Override
		public String toString() {
			return value;
		}
	}

	private ManuallyBuildStep parseMouseStep(String action, List<String> params) throws InvalidSuppliedBuildStepException {
		if (action.equals(MouseAction.CLICK.toString())) {
			verify(params, Arrays.asList(Long.class, NonNegativeLong.class, NonNegativeLong.class));
			return MouseClickStep.of(Integer.parseInt(params.get(0)), Integer.parseInt(params.get(1)), Integer.parseInt(params.get(2)));
		} else if (action.equals(MouseAction.CLICK_CURRENT_POSITION.toString())) {
			verify(params, Arrays.asList(Long.class));
			return MouseClickCurrentPositionStep.of(Integer.parseInt(params.get(0)));
		} else if (action.equals(MouseAction.MOVE_BY.toString())) {
			verify(params, Arrays.asList(Long.class, Long.class));
			return MouseMoveByStep.of(Integer.parseInt(params.get(0)), Integer.parseInt(params.get(1)));
		} else if (action.equals(MouseAction.MOVE.toString())) {
			verify(params, Arrays.asList(NonNegativeLong.class, NonNegativeLong.class));
			return MouseMoveStep.of(Integer.parseInt(params.get(0)), Integer.parseInt(params.get(1)));
		} else if (action.equals(MouseAction.PRESS_CURRENT_POSITION.toString())) {
			verify(params, Arrays.asList(Long.class));
			return MousePressCurrentPositionStep.of(Integer.parseInt(params.get(0)));
		} else if (action.equals(MouseAction.RELEASE_CURRENT_POSITION.toString())) {
			verify(params, Arrays.asList(Long.class));
			return MouseReleaseCurrentPositionStep.of(Integer.parseInt(params.get(0)));
		} else {
			throw new InvalidSuppliedBuildStepException("Unknown mouse action " + action + ".");
		}
	}

	private static enum KeyboardAction implements Action {
		PRESS_KEY("press"),
		RELEASE_KEY("release"),
		TYPE_KEY("type"),
		TYPE_STRING_KEY("type_string")
		;

		private String value;
		private KeyboardAction(String s) { value = s; }
		@Override
		public String toString() {
			return value;
		}
	}

	private ManuallyBuildStep parseKeyboardStep(String action, List<String> params) throws InvalidSuppliedBuildStepException {
		if (action.equals(KeyboardAction.PRESS_KEY.toString())) {
			verify(params, Arrays.asList(Long.class));
			return KeyboardPressKeyStep.of(Integer.parseInt(params.get(0)));
		} else if (action.equals(KeyboardAction.RELEASE_KEY.toString())) {
			verify(params, Arrays.asList(Long.class));
			return KeyboardReleaseKeyStep.of(Integer.parseInt(params.get(0)));
		} else if (action.equals(KeyboardAction.TYPE_KEY.toString())) {
			verify(params, Arrays.asList(Long.class));
			return KeyboardTypeKeyStep.of(Integer.parseInt(params.get(0)));
		} else if (action.equals(KeyboardAction.TYPE_STRING_KEY.toString())) {
			verify(params, Arrays.asList(String.class));
			return KeyboardTypeStringStep.of(params.get(0));
		} else {
			throw new InvalidSuppliedBuildStepException("Unknown keyboard action " + action + ".");
		}
	}

	private static enum ControllerAction implements Action {
		WAIT("blocking_wait");

		private String value;
		private ControllerAction(String s) { value = s; }
		@Override
		public String toString() {
			return value;
		}
	}

	private ManuallyBuildStep parseControllerStep(String action, List<String> params) throws InvalidSuppliedBuildStepException {
		if  (action.equals(ControllerAction.WAIT.toString())) {
			verify(params, Arrays.asList(NonNegativeLong.class));
			return ControllerDelayStep.of(Integer.parseInt(params.get(0)));
		}
		throw new InvalidSuppliedBuildStepException("Unknown controller action " + action + ".");
	}

	private void verify(List<String> params, List<Class<?>> required) throws InvalidSuppliedBuildStepException {
		if (params.size() != required.size()) {
			throw new InvalidSuppliedBuildStepException("Got " + params.size() + " parameters but expected " + required.size() + ".");
		}

		Iterator<Class<?>> requiredIterator = required.iterator();
		for (ListIterator<String> iterator = params.listIterator(); iterator.hasNext();) {
			Class<?> requiredClass = requiredIterator.next();
			if (requiredClass != Long.class && requiredClass != NonNegativeLong.class && requiredClass != String.class) {
				throw new InvalidSuppliedBuildStepException("Only accepting Long & String, but got " + requiredClass + ".");
			}

			int index = iterator.nextIndex();
			String param = iterator.next();
			if (requiredClass == Long.class || requiredClass == NonNegativeLong.class) {
				try {
					long number = Long.parseLong(param);
					if (requiredClass == NonNegativeLong.class && number < 0) {
						throw new InvalidSuppliedBuildStepException("Parameter #" + index + " must be a non-negative integer but got " + param);
					}
				} catch (NumberFormatException e) {
					throw new InvalidSuppliedBuildStepException("Parameter #" + index + " must be an integer but got " + param);
				}
			}
		}
	}

	private static class NonNegativeLong {}

	private ManuallyBuildActionParametersParser() {}
}
