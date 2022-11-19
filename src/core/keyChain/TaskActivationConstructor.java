package core.keyChain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import utilities.StringUtilities;

public class TaskActivationConstructor {

	private LinkedList<ButtonStroke> strokes;
	private List<KeyChain> keyChains;
	private List<KeySequence> keySequences;
	private List<ActivationPhrase> phrases;
	private List<MouseGesture> mouseGestures;
	private List<SharedVariablesActivation> variables;
	private GlobalActivation globalActivation;

	private boolean listening;

	private Config config;

	public TaskActivationConstructor(TaskActivation reference) {
		this(reference, Config.of());
	}

	public TaskActivationConstructor(TaskActivation reference, Config config) {
		strokes = new LinkedList<>();
		keyChains = new ArrayList<>(reference.getHotkeys());
		keySequences = new ArrayList<>(reference.getKeySequences());
		phrases = new ArrayList<>(reference.getPhrases());
		mouseGestures = new ArrayList<>(reference.getMouseGestures());
		variables = new ArrayList<>(reference.getVariables());
		globalActivation = reference.getGlobalActivation();

		this.config = config;
	}

	public String getStrokes() {
		return StringUtilities.join(strokes.stream().map(ButtonStroke::toString).collect(Collectors.toList()), " + ");
	}

	public void clearStrokes() {
		strokes.clear();
	}

	public TaskActivation getActivation() {
		return TaskActivation.newBuilder()
				.withHotKeys(keyChains)
				.withKeySequence(keySequences)
				.withPhrases(phrases)
				.withMouseGestures(mouseGestures)
				.withVariables(variables)
				.withGlobalActivation(globalActivation)
				.build();
	}

	public List<KeyChain> getKeyChains() {
		return keyChains;
	}

	public List<KeySequence> getKeySequences() {
		return keySequences;
	}

	public List<ActivationPhrase> getPhrases() {
		return phrases;
	}

	public List<SharedVariablesActivation> getVariables() {
		return variables;
	}

	public void startListening() {
		listening = true;
	}

	public void stopListening() {
		listening = false;
	}

	public boolean isListening() {
		return listening;
	}

	public void onStroke(KeyStroke stroke) {
		if (!listening) {
			return;
		}
		strokes.add(stroke);
		if (strokes.size() > config.maxStrokes) {
			strokes.removeFirst();
		}
	}

	public void addMouseKey(MouseKey mouseKey) {
		if (!listening) {
			return;
		}

		strokes.add(mouseKey);
		if (strokes.size() > config.maxStrokes) {
			strokes.removeFirst();
		}
	}

	public void addAsKeyChain() {
		if (strokes.isEmpty()) {
			return;
		}
		keyChains.add(new KeyChain(strokes));
		strokes = new LinkedList<>();
	}

	public void removeKeyChain(int index) {
		if (index < 0 || index >= keyChains.size()) {
			return;
		}
		keyChains.remove(index);
	}

	public void addAsKeySequence() {
		if (strokes.isEmpty()) {
			return;
		}
		keySequences.add(new KeySequence(strokes));
		strokes = new LinkedList<>();
	}

	public void removeKeySequence(int index) {
		if (index < 0 || index >= keySequences.size()) {
			return;
		}
		keySequences.remove(index);
	}

	public void addPhrase(String phrase) {
		if (phrase.isEmpty()) {
			return;
		}
		phrases.add(ActivationPhrase.of(phrase));
	}

	public void removePhrase(int index) {
		if (index < 0 || index >= phrases.size()) {
			return;
		}
		phrases.remove(index);
	}

	public void setMouseGestures(Collection<MouseGesture> gestures) {
		mouseGestures.clear();
		mouseGestures.addAll(gestures);
	}

	public void addSharedVariables(Collection<SharedVariablesActivation> sharedVariablesActivations) {
		variables.addAll(sharedVariablesActivations);
	}

	public void removeSharedVariable(int index) {
		variables.remove(index);
	}

	public void setGlobalKeyReleased(boolean value) {
		globalActivation = GlobalActivation.Builder.fromGlobalActivation(globalActivation).onKeyReleased(value).build();
	}

	public void setGlobalKeyPressed(boolean value) {
		globalActivation = GlobalActivation.Builder.fromGlobalActivation(globalActivation).onKeyPressed(value).build();
	}

	public Config getConfig() {
		return config;
	}

	public static class Config {
		private boolean disableKeyChain;
		private boolean disableKeySequence;
		private boolean disablePhrase;
		private boolean disableMouseGesture;
		private boolean disableVariablesActivation;
		private boolean disableGlobalKeyActions;

		private int maxStrokes = Integer.MAX_VALUE;

		public static Config of() {
			return new Config();
		}

		public static Config ofRestricted() {
			Config config = new Config();
			config.disableKeyChain = true;
			config.disableKeySequence = true;
			config.disablePhrase = true;
			config.disableMouseGesture = true;
			config.disableVariablesActivation = true;
			config.disableGlobalKeyActions = true;
			return config;
		}

		public int getMaxStrokes() {
			return maxStrokes;
		}

		public Config setMaxStrokes(int maxStrokes) {
			this.maxStrokes = maxStrokes;
			return this;
		}

		public boolean isDisableKeyChain() {
			return disableKeyChain;
		}

		public Config setDisableKeyChain(boolean disableKeyChain) {
			this.disableKeyChain = disableKeyChain;
			return this;
		}

		public boolean isDisableKeySequence() {
			return disableKeySequence;
		}

		public Config setDisableKeySequence(boolean disableKeySequence) {
			this.disableKeySequence = disableKeySequence;
			return this;
		}

		public boolean isDisablePhrase() {
			return disablePhrase;
		}

		public Config setDisablePhrase(boolean disablePhrase) {
			this.disablePhrase = disablePhrase;
			return this;
		}

		public boolean isDisableMouseGesture() {
			return disableMouseGesture;
		}

		public Config setDisableMouseGesture(boolean disableMouseGesture) {
			this.disableMouseGesture = disableMouseGesture;
			return this;
		}

		public boolean isDisableVariablesActivation() {
			return disableVariablesActivation;
		}

		public Config setDisableVariablesActivation(boolean disableVariablesActivation) {
			this.disableVariablesActivation = disableVariablesActivation;
			return this;
		}

		public boolean isDisabledGlobalKeyAction() {
			return disableGlobalKeyActions;
		}

		public Config setDisabledGlobalKeyAction(boolean disableGlobalKeyActions) {
			this.disableGlobalKeyActions = disableGlobalKeyActions;
			return this;
		}
	}
}
