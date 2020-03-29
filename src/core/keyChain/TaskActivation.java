package core.keyChain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.userDefinedTask.internals.SharedVariablesSubscription;
import utilities.json.IJsonable;
import utilities.json.JSONUtility;


/**
 * Represents an entity that activates a {@link core.userDefinedTask.UserDefinedAction}.
 */
public class TaskActivation implements IJsonable {

	private static final Logger LOGGER = Logger.getLogger(TaskActivation.class.getName());

	private Set<KeyChain> hotkeys;
	private Set<MouseGesture> mouseGestures;
	private Set<KeySequence> keySequences;
	private Set<ActivationPhrase> phrases;
	private Set<SharedVariablesActivation> variables;

	private GlobalActivation globalActivation;

	private TaskActivation(Builder builder) {
		hotkeys = builder.hotkeys;
		mouseGestures = builder.mouseGestures;
		keySequences = builder.keySequences;
		phrases = builder.phrases;
		variables = builder.variables;
		globalActivation = builder.globalActivation;
	}

	/**
	 * @param hotkeys the hotkey set to set
	 */
	private final void setHotKeys(Set<KeyChain> hotkeys) {
		this.hotkeys = new HashSet<>();
		this.hotkeys.addAll(hotkeys);
	}

	/**
	 * @return the set of key chains associated with this activation entity.
	 */
	public final Set<KeyChain> getHotkeys() {
		if (hotkeys == null) {
			hotkeys = new HashSet<KeyChain>();
		}
		return hotkeys;
	}

	/**
	 * @return an arbitrary {@link KeyChain} from the set of keychains, or null if the set is empty.
	 */
	public final KeyChain getFirstHotkey() {
		Set<KeyChain> hotkeys = getHotkeys();
		if (hotkeys.isEmpty()) {
			return null;
		} else {
			return hotkeys.iterator().next();
		}
	}

	/**
	 * @param mouseGestures set of mouse gestures to set.
	 */
	private final void setMouseGestures(Set<MouseGesture> mouseGestures) {
		this.mouseGestures = new HashSet<>();
		this.mouseGestures.addAll(mouseGestures);
	}

	/**
	 * @return set of mouse gestures associated with this activation entity.
	 */
	public final Set<MouseGesture> getMouseGestures() {
		if (mouseGestures == null) {
			mouseGestures = new HashSet<>();
		}

		return mouseGestures;
	}

	/**
	 * @return an arbitrary {@link MouseGesture} from the set of gestures, or null if the set is empty.
	 */
	public final MouseGesture getFirstMouseGesture() {
		Set<MouseGesture> gestures = getMouseGestures();
		if (gestures.isEmpty()) {
			return null;
		} else {
			return gestures.iterator().next();
		}
	}

	/**
	 * @param keySequences set of key sequences to set.
	 */
	private final void setKeySequences(Set<KeySequence> keySequences) {
		this.keySequences = new HashSet<>();
		this.keySequences.addAll(keySequences);
	}

	/**
	 * @return set of key sequences associated with this activation entity.
	 */
	public final Set<KeySequence> getKeySequences() {
		if (keySequences == null) {
			return new HashSet<>();
		}

		return keySequences;
	}

	/**
	 * @return an arbitrary {@link KeySequence} from the set of gestures, or null if the set is empty.
	 */
	public final KeySequence getFirstKeySequence() {
		Set<KeySequence> keySequences = getKeySequences();
		if (keySequences.isEmpty()) {
			return null;
		} else {
			return keySequences.iterator().next();
		}
	}

	/**
	 * @param phrases set of key phrases to set.
	 */
	private void setPhrases(Set<ActivationPhrase> phrases) {
		this.phrases.clear();
		this.phrases.addAll(phrases);
	}

	/**
	 * @return set of phrases associated with this activation entity.
	 */
	public final Set<ActivationPhrase> getPhrases() {
		if (phrases == null) {
			return new HashSet<>();
		}

		return phrases;
	}

	/**
	 * @return an arbitrary phrase from the set of pharses, or null if the set is empty.
	 */
	public final ActivationPhrase getFirsPhrase() {
		Set<ActivationPhrase> phrases = getPhrases();
		if (phrases.isEmpty()) {
			return null;
		} else {
			return phrases.iterator().next();
		}
	}

	/**
	 * @param phrases set of key phrases to set.
	 */
	private void setVariables(Set<SharedVariablesActivation> variables) {
		this.variables.clear();
		this.variables.addAll(variables);
	}

	/**
	 * @return set of variables associated with this activation entity.
	 */
	public final Set<SharedVariablesActivation> getVariables() {
		if (variables == null) {
			return new HashSet<>();
		}

		return variables;
	}

	/**
	 * @return an arbitrary variable from the set of variables, or null if the set is empty.
	 */
	public final SharedVariablesActivation getFirstVariable() {
		Set<SharedVariablesActivation> variables = getVariables();
		if (variables.isEmpty()) {
			return null;
		} else {
			return variables.iterator().next();
		}
	}

	/**
	 * @return the global activation configuration for this activation.
	 */
	public final GlobalActivation getGlobalActivation() {
		if (globalActivation == null) {
			return GlobalActivation.newBuilder().build();
		}
		return globalActivation;
	}

	/**
	 * @param globalActivation configuration to set.
	 */
	private final void setGlobalActivation(GlobalActivation globalActivation) {
		this.globalActivation = globalActivation;
	}

	/**
	 * Copy the content of the other {@link TaskActivation} to this object.
	 *
	 * @param other other task activation whose content will be copied from.
	 */
	public final void copy(TaskActivation other) {
		setHotKeys(other.getHotkeys());
		setMouseGestures(other.getMouseGestures());
		setKeySequences(other.getKeySequences());
		setPhrases(other.getPhrases());
		setVariables(other.getVariables());
		setGlobalActivation(other.getGlobalActivation());
	}

	/**
	 * Check if this activation is empty (i.e. no event for activation).
	 */
	public final boolean isEmpty() {
		return getHotkeys().isEmpty() && getMouseGestures().isEmpty() && getKeySequences().isEmpty() && getPhrases().isEmpty();
	}

	/**
	 * Returns a representative string for this activation.
	 * Iterating through all types of activations and select one entry at random.
	 */
	public String getRepresentativeString() {
		if (!getHotkeys().isEmpty()) {
			return "{" + getHotkeys().iterator().next().toString() + "}";
		}
		if (!getKeySequences().isEmpty()) {
			return "<" + getKeySequences().iterator().next().toString() + ">";
		}
		if (!getPhrases().isEmpty()) {
			return "(" + getPhrases().iterator().next().toString() + ")";
		}
		if (!getMouseGestures().isEmpty()) {
			return "[" + getMouseGestures().iterator().next().toString() + "]";
		}
		if (!getVariables().isEmpty()) {
			SharedVariablesSubscription var = getVariables().iterator().next().getVariable();
			if (var.isAll()) {
				return "-(all)-";
			}
			if (var.isAllForNamespace()) {
				return "-(" + var.getNamespace() + ") - (all)-";
			}

			return "-(" + var.getNamespace() + ") - (" + var.getName() + ")-";
		}

		return new KeyChain().toString();
	}

	@Override
	public JsonRootNode jsonize() {
		return JsonNodeFactories.object(
				JsonNodeFactories.field("hotkey", JsonNodeFactories.array(JSONUtility.listToJson(getHotkeys()))),
				JsonNodeFactories.field("key_sequence", JsonNodeFactories.array(JSONUtility.listToJson(getKeySequences()))),
				JsonNodeFactories.field("mouse_gesture", JsonNodeFactories.array(JSONUtility.listToJson(getMouseGestures()))),
				JsonNodeFactories.field("phrases", JsonNodeFactories.array(JSONUtility.listToJson(getPhrases()))),
				JsonNodeFactories.field("variables", JsonNodeFactories.array(JSONUtility.listToJson(getVariables()))),
				JsonNodeFactories.field("global_activation", globalActivation.jsonize()));
	}

	/**
	 * Construct a new object from a json node.
	 *
	 * @param node json node to parse
	 * @return the new object with content parsed, or null if cannot parse.
	 */
	public static TaskActivation parseJSON(JsonNode node) {
		try {
			List<JsonNode> hotkeysNode = node.getArrayNode("hotkey");
			List<JsonNode> keySequenceNodes = node.isArrayNode("key_sequence") ? node.getArrayNode("key_sequence") : new ArrayList<>();
			List<JsonNode> mouseGestureNode = node.getArrayNode("mouse_gesture");
			List<JsonNode> phrasesNodes = node.isArrayNode("phrases") ? node.getArrayNode("phrases") : new ArrayList<>();
			List<JsonNode> variablesNodes = node.getArrayNode("variables");

			Set<KeyChain> keyChains = new HashSet<>();
			for (JsonNode hotkeyNode : hotkeysNode) {
				KeyChain newKeyChain = KeyChain.parseJSON(hotkeyNode.getArrayNode());
				if (newKeyChain == null) {
					LOGGER.log(Level.WARNING, "Cannot parse key chain " + hotkeyNode);
				} else {
					keyChains.add(newKeyChain);
				}
			}

			Set<KeySequence> keySequences = new HashSet<>();
			for (JsonNode keySequenceNode : keySequenceNodes) {
				KeySequence newkeySequence = KeySequence.parseJSON(keySequenceNode.getArrayNode());
				if (newkeySequence == null) {
					LOGGER.log(Level.WARNING, "Cannot parse key chain " + keySequenceNode);
				} else {
					keySequences.add(newkeySequence);
				}
			}

			Set<MouseGesture> gestures = MouseGesture.parseJSON(mouseGestureNode);

			Set<ActivationPhrase> phrases = new HashSet<>();
			for (JsonNode phraseNode : phrasesNodes) {
				ActivationPhrase phrase = ActivationPhrase.parseJSON(phraseNode);
				if (phrase == null) {
					LOGGER.log(Level.WARNING, "Cannot parse phrase " + phraseNode);
				} else {
					phrases.add(phrase);
				}
			}

			Set<SharedVariablesActivation> variables = new HashSet<>();
			for (JsonNode variableNode : variablesNodes) {
				SharedVariablesActivation variable = SharedVariablesActivation.parseJSON(variableNode);
				if (variable == null) {
					LOGGER.log(Level.WARNING, "Cannot parse variable node " + variableNode);
				} else {
					variables.add(variable);
				}
			}

			GlobalActivation globalActivation = node.isNode("global_activation")
					? GlobalActivation.parseJSON(node.getNode("global_activation"))
					: GlobalActivation.newBuilder().build();
			if (globalActivation == null) {
				globalActivation = GlobalActivation.newBuilder().build();
			}

			TaskActivation output = TaskActivation.newBuilder()
										.withHotKeys(keyChains)
										.withKeySequence(keySequences)
										.withMouseGestures(gestures)
										.withPhrases(phrases)
										.withVariables(variables)
										.withGlobalActivation(globalActivation)
										.build();
			return output;
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Exception while parsing task activation.", e);
			return null;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(
				getGlobalActivation(),
				getHotkeys(),
				getKeySequences(),
				getMouseGestures(),
				getPhrases(),
				getVariables());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TaskActivation other = (TaskActivation) obj;
		return getGlobalActivation().equals(other.getGlobalActivation())
				&& getHotkeys().equals(other.getHotkeys())
				&& getKeySequences().equals(other.getKeySequences())
				&& getMouseGestures().equals(other.getMouseGestures())
				&& getPhrases().equals(other.getPhrases())
				&& getVariables().equals(other.getVariables());
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	/**
	 * Builder for enclosing class.
	 */
	public static class Builder {

		private Set<KeyChain> hotkeys;
		private Set<MouseGesture> mouseGestures;
		private Set<KeySequence> keySequences;
		private Set<ActivationPhrase> phrases;
		private Set<SharedVariablesActivation> variables;
		private GlobalActivation globalActivation;

		private Builder() {
			hotkeys = new HashSet<>();
			mouseGestures = new HashSet<>();
			keySequences = new HashSet<>();
			phrases = new HashSet<>();
			variables = new HashSet<>();
			globalActivation = GlobalActivation.newBuilder().build();
		}

		public Builder addHotKeys(KeyChain... keys) {
			hotkeys.addAll(Arrays.asList(keys));
			return this;
		}

		public Builder withHotKey(KeyChain key) {
			hotkeys.clear();
			hotkeys.add(key);
			return this;
		}

		public Builder withHotKeys(Collection<KeyChain> keys) {
			this.hotkeys.clear();
			this.hotkeys.addAll(keys);
			return this;
		}

		public Builder addMouseGesture(MouseGesture... gestures) {
			mouseGestures.addAll(Arrays.asList(gestures));
			return this;
		}

		public Builder withMouseGesture(MouseGesture gesture) {
			mouseGestures.clear();
			mouseGestures.add(gesture);
			return this;
		}

		public Builder withMouseGestures(Collection<MouseGesture> gestures) {
			this.mouseGestures.clear();
			this.mouseGestures.addAll(gestures);
			return this;
		}

		public Builder addKeySequence(KeySequence... keySequences) {
			this.keySequences.addAll(Arrays.asList(keySequences));
			return this;
		}

		public Builder withKeySequence(KeySequence keySequences) {
			this.keySequences.clear();
			this.keySequences.add(keySequences);
			return this;
		}

		public Builder withKeySequence(Collection<KeySequence> keySequences) {
			this.keySequences.clear();
			this.keySequences.addAll(keySequences);
			return this;
		}

		public Builder addPhrases(ActivationPhrase... phrases) {
			this.phrases.addAll(Arrays.asList(phrases));
			return this;
		}

		public Builder withPhrase(ActivationPhrase phrase) {
			this.phrases.clear();
			this.phrases.add(phrase);
			return this;
		}

		public Builder withPhrases(Collection<ActivationPhrase> phrases) {
			this.phrases.clear();
			this.phrases.addAll(phrases);
			return this;
		}

		public Builder addVariables(SharedVariablesActivation... variables) {
			this.variables.addAll(Arrays.asList(variables));
			return this;
		}

		public Builder withVariable(SharedVariablesActivation variable) {
			this.variables.clear();
			this.variables.add(variable);
			return this;
		}

		public Builder withVariables(Collection<SharedVariablesActivation> variables) {
			this.variables.clear();
			this.variables.addAll(variables);
			return this;
		}

		public Builder withGlobalActivation(GlobalActivation globalActivation) {
			this.globalActivation = globalActivation;
			return this;
		}

		public TaskActivation build() {
			return new TaskActivation(this);
		}
	}
}
